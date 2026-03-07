package org.elnix.dragonlauncher.common.logging

import android.util.Log
import timber.log.Timber

object DragonLogManager {
    private var isLoggingEnabled = false
    private var fileTree: FileLoggingTree? = null

    fun init(ctx: android.content.Context) {
        // Arbre pour Logcat (uniquement en debug)
        if (Log.isLoggable("DragonLauncher", Log.DEBUG)) {
            Timber.plant(Timber.DebugTree())
        }

        // Initialisation de l'arbre pour les fichiers
        fileTree = FileLoggingTree(ctx)
        updateLoggingState()
    }

    fun enableLogging(enable: Boolean) {
        if (isLoggingEnabled == enable) return
        isLoggingEnabled = enable
        updateLoggingState()
    }

    private fun updateLoggingState() {
        val tree = fileTree ?: return
        val plantedTrees = Timber.forest()
        if (isLoggingEnabled) {
            if (tree !in plantedTrees) {
                Timber.plant(tree)
            }
        } else {
            if (tree in plantedTrees) {
                Timber.uproot(tree)
            }
        }
    }

    fun getAllLogFiles(): List<java.io.File> {
        return fileTree?.getAllLogFiles() ?: emptyList()
    }

    fun clearLogs() {
        fileTree?.clearAllLogs()
    }

    fun readLogFile(file: java.io.File): String {
        return try {
            file.readText()
        } catch (e: Exception) {
            "Failed to read log file: ${e.message}"
        }
    }

    fun deleteLogFile(file: java.io.File) {
        try {
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
