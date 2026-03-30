package org.elnix.dragonlauncher.common.logging

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentLinkedQueue

class FileLoggingTree(ctx: Context) : Timber.Tree() {

    private val logDir = File(ctx.filesDir, "logs").apply { if (!exists()) mkdirs() }
    private val logQueue = ConcurrentLinkedQueue<String>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var currentSessionFile: File? = null
    private val maxFileSizeBytes = 1024 * 1024L // 1MB

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    private val fileDateFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    init {
        rotateFile()
        startFlushJob()
    }

    private fun rotateFile() {
        currentSessionFile = File(logDir, "dragon_logs_${fileDateFormatter.format(Date())}.txt")
        cleanOldLogs()
    }

    private fun cleanOldLogs() {
        val files = logDir.listFiles()?.filter { it.name.startsWith("dragon_logs_") } ?: return
        if (files.size > 5) {
            files.sortedBy { it.lastModified() }.dropLast(5).forEach { it.delete() }
        }
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority < Log.INFO) return

        val timestamp = dateFormatter.format(Date())
        val priorityStr = when (priority) {
            Log.VERBOSE -> "V"
            Log.DEBUG -> "D"
            Log.INFO -> "I"
            Log.WARN -> "W"
            Log.ERROR -> "E"
            Log.ASSERT -> "A"
            else -> "?"
        }

        val logLine = "[$timestamp] $priorityStr/${tag ?: "NoTag"}: $message"
        val fullLog = if (t != null) "$logLine\n${Log.getStackTraceString(t)}" else logLine

        logQueue.add(fullLog)

        if (logQueue.size >= 50) {
            flush()
        }
    }

    private fun startFlushJob() {
        scope.launch {
            while (isActive) {
                delay(5000) // Flush toutes les 5 secondes même si peu de logs
                flush()
            }
        }
    }

    private fun flush() {
        if (logQueue.isEmpty()) return

        val logsToWrite = mutableListOf<String>()
        while (logQueue.isNotEmpty()) {
            logQueue.poll()?.let { logsToWrite.add(it) }
        }

        if (logsToWrite.isEmpty()) return

        scope.launch {
            try {
                val file = currentSessionFile ?: return@launch
                if (file.length() > maxFileSizeBytes) rotateFile()

                FileWriter(file, true).use { writer ->
                    logsToWrite.forEach {
                        writer.append(it).append("\n")
                    }
                }
            } catch (e: Exception) {
                Timber.tag("FileLoggingTree").e(e, "Error writing logs to file")
            }
        }
    }

    fun getAllLogFiles(): List<File> {
        return logDir.listFiles()?.filter { it.name.startsWith("dragon_logs_") }?.toList() ?: emptyList()
    }

    fun clearAllLogs() {
        logDir.listFiles()?.forEach { it.delete() }
        rotateFile()
    }
}
