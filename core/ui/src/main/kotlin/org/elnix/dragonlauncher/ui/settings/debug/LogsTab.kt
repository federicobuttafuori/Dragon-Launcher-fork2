@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.settings.debug

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.logging.DragonLogManager
import org.elnix.dragonlauncher.logging.logD
import org.elnix.dragonlauncher.logging.logE
import org.elnix.dragonlauncher.common.utils.Constants.Logging.LOGS_TAG
import org.elnix.dragonlauncher.common.utils.copyToClipboard
import org.elnix.dragonlauncher.common.utils.detectSystemLauncher
import org.elnix.dragonlauncher.common.utils.formatDateTime
import org.elnix.dragonlauncher.common.utils.getVersionCode
import org.elnix.dragonlauncher.common.utils.getVersionName
import org.elnix.dragonlauncher.common.utils.isDefaultLauncher
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.services.ExtensionManager
import org.elnix.dragonlauncher.settings.stores.DebugSettingsStore
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.components.dragon.DragonIconButton
import org.elnix.dragonlauncher.ui.components.settings.SettingsSwitchRow
import org.elnix.dragonlauncher.ui.dialogs.UserValidation
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsScaffold
import java.io.File

@Composable
fun LogsTab(
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var logs by remember { mutableStateOf("") }
    var selectedFile by remember { mutableStateOf<File?>(null) }

    var refreshTrigger by remember { mutableIntStateOf(0) }
    val logFiles by produceState(initialValue = emptyList(), ctx, refreshTrigger) {
        value = DragonLogManager.getAllLogFiles()
    }

    var showDeleteDialog by remember { mutableStateOf<File?>(null) }

    val windowInfo = LocalWindowInfo.current
    val am = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memInfo = ActivityManager.MemoryInfo()
    am.getMemoryInfo(memInfo)
    val currentLauncher = detectSystemLauncher(ctx)
    val isDefault = ctx.isDefaultLauncher
    val versionName = ctx.getVersionName()
    val versionCode = ctx.getVersionCode()

    // Build extension list by parsing the registry JSON directly (robust to field names)
    var finalExtensionText = "No extensions installed"
    try {
        val registryContent = ctx.assets.open("extensions-registry.json").bufferedReader().readText()
        val root = Json.parseToJsonElement(registryContent)
        val lines = ArrayList<String>()

        if (root is JsonArray) {
            for (elem in root) {
                try {
                    val obj = elem.jsonObject
                    val pkgValue = obj["package"]?.jsonPrimitive?.contentOrNull
                    val nameValue = obj["name"]?.jsonPrimitive?.contentOrNull ?: "Unknown"

                    if (!pkgValue.isNullOrEmpty()) {
                        if (ExtensionManager.isExtensionInstalled(ctx, pkgValue)) {
                            val pkgInfo = try {
                                ctx.packageManager.getPackageInfo(pkgValue, 0)
                            } catch (_: Exception) {
                                null
                            }

                            val versionStr = pkgInfo?.versionName ?: "unknown"
                            lines.add("$nameValue ($versionStr)")
                        }
                    }
                } catch (_: Exception) {
                }
            }
        }

        if (lines.isNotEmpty()) finalExtensionText = lines.joinToString("\n")
    } catch (_: Exception) {
        // registry not available or parse failed -> leave default text
    }

    val deviceDetails = remember {
        buildString {
            appendLine("─── DEVICE DETAILS ───")
            appendLine("System: ${Build.MANUFACTURER} ${Build.MODEL} (${Build.PRODUCT})")
            appendLine("OS: Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
            if (Build.VERSION.SECURITY_PATCH.isNotEmpty()) {
                appendLine("Security Patch: ${Build.VERSION.SECURITY_PATCH}")
            }
            appendLine("Arch: ${Build.SUPPORTED_ABIS.firstOrNull() ?: "unknown"}")
            appendLine("Display: ${windowInfo.containerSize.width}x${windowInfo.containerSize.height}px")
            appendLine(
                "RAM: %.1fGB used / %.1fGB total (%d%% available)".format(
                    (memInfo.totalMem - memInfo.availMem) / 1024.0 / 1024 / 1024,
                    memInfo.totalMem / 1024.0 / 1024 / 1024,
                    memInfo.availMem * 100 / memInfo.totalMem
                )
            )
            appendLine("Default Launcher: ${if (isDefault) "Yes" else "No ($currentLauncher)"}")
            appendLine("App version: $versionName ($versionCode)")

            appendLine("\n─── EXTENSIONS ───")
            appendLine(finalExtensionText)

            appendLine("\n─── PERMISSIONS ───")
            try {
                val info = ctx.packageManager.getPackageInfo(ctx.packageName, PackageManager.GET_PERMISSIONS)
                info.requestedPermissions?.forEachIndexed { index, perm ->
                    val flags = info.requestedPermissionsFlags
                    val granted = (flags != null && (flags[index] and 0x00000002) != 0) ||
                            ContextCompat.checkSelfPermission(ctx, perm) == PackageManager.PERMISSION_GRANTED
                    appendLine("${perm.substringAfterLast(".")}: ${if (granted) "✅" else "❌"}")
                }
            } catch (e: Exception) {
                appendLine("Error reading permissions: $e")
            }
        }
    }

    SettingsScaffold(
        title = "Logs",
        onBack = onBack,
        helpText = "Logs, need more info?",
        onReset = {
            DragonLogManager.clearLogs()
            selectedFile = null
            logs = ""
            refreshTrigger++
        },
        resetText = "Clear all logs",


        otherIcons = arrayOf(
            Triple(
                { refreshTrigger++; ctx.showToast("Refreshing...") },
                Icons.Default.Refresh,
                stringResource(R.string.refresh)
            )
        ),
        content = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = AppObjectsColors.cardColors()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Device Information",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            DragonIconButton(
                                onClick = {
                                    ctx.copyToClipboard(deviceDetails)
                                    ctx.showToast("Device info copied")
                                },
                                modifier = Modifier.size(20.dp),
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Info"
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        SelectionContainer {
                            Text(
                                text = deviceDetails,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                SettingsSwitchRow(
                    setting = DebugSettingsStore.enableLogging,
                    title = "Enable logging",
                    description = "Store all logs in memory, and can be printed or exported",
                ) {
                    DragonLogManager.enableLogging(it)
                }

                Button(
                    onClick = {
                        DragonLogManager.clearLogs()
                        selectedFile = null
                        logs = ""
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Clear All Logs")
                }


                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 600.dp)
                ) {
                    items(logFiles) { file ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedFile = file
                                    logs = "Loading…"
                                    scope.launch {
                                        logs = DragonLogManager.readLogFile(file)
                                    }
                                },
                            colors = AppObjectsColors.cardColors()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = file.name,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "${(file.length() / 1024).toInt()}KB • ${
                                            file.lastModified().formatDateTime()
                                        }",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Row(
                                    modifier = Modifier.padding(5.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    DragonIconButton(
                                        onClick = {
                                            showDeleteDialog = file
                                        },
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete"
                                    )

                                    DragonIconButton(
                                        onClick = {
                                            exportLogFile(ctx, file)
                                        },
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Export"
                                    )
                                }
                            }
                        }
                    }
                }

                if (logs.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = AppObjectsColors.cardColors()
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = selectedFile?.name ?: "Logs",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Row {
                                    DragonIconButton(
                                        onClick = {
                                            try {
                                                ctx.copyToClipboard(logs)
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                ctx.showToast("Failed to copy to clipboard, probably too long")
                                            }
                                        },
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy All"
                                    )

                                    DragonIconButton(
                                        onClick = {
                                            logs = ""
                                            selectedFile = null
                                        },
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close"
                                    )
                                }
                            }

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp)
                                    .padding(16.dp)
                            ) {
                                items(logs.lines()) { line ->
                                    SelectionContainer {
                                        Text(
                                            text = line,
                                            softWrap = false,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .horizontalScroll(rememberScrollState()),

                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )

    if (showDeleteDialog != null) {
        val fileToDelete = showDeleteDialog!!

        UserValidation(
            title = "Delete file ${fileToDelete.name}",
            message = "THis can't be undone",
            onDismiss = { showDeleteDialog = null }
        ) {
            DragonLogManager.deleteLogFile(fileToDelete)
            showDeleteDialog = null
        }
    }
}

private fun exportLogFile(ctx: Context, file: File) {
    try {
        val cacheDir = ctx.cacheDir
        val shareFile = File(cacheDir, file.name)
        file.copyTo(shareFile, overwrite = true)

        val uri = FileProvider.getUriForFile(
            ctx,
            "${ctx.packageName}.fileprovider",
            shareFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Dragon Logs - ${file.name}")
            putExtra(Intent.EXTRA_TEXT, "Dragon Launcher logs")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }


        ctx.startActivity(Intent.createChooser(shareIntent, "Share ${file.name}"))
        logD(LOGS_TAG) { " Share opened: ${file.name} (${shareFile.absolutePath})" }

    } catch (e: SecurityException) {
        logE(LOGS_TAG, e) { "FileProvider not configured" }
        val content = DragonLogManager.readLogFile(file)
        val textIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content)
            putExtra(Intent.EXTRA_SUBJECT, "Dragon Logs - ${file.name}")
        }
        ctx.startActivity(Intent.createChooser(textIntent, "Share logs (text)"))
    } catch (e: Exception) {
        logE(LOGS_TAG, e) { "Share failed" }
    }
}
