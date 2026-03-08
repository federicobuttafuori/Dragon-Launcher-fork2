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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.InputStreamReader
import org.elnix.dragonlauncher.common.serializables.ExtensionModel
import kotlinx.coroutines.delay
import kotlinx.serialization.json.*
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.logging.DragonLogManager
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.utils.copyToClipboard
import org.elnix.dragonlauncher.common.utils.detectSystemLauncher
import org.elnix.dragonlauncher.common.utils.formatDateTime
import org.elnix.dragonlauncher.common.utils.isDefaultLauncher
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.services.ExtensionManager
import org.elnix.dragonlauncher.settings.stores.DebugSettingsStore
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.components.dragon.DragonIconButton
import org.elnix.dragonlauncher.ui.components.settings.SettingsSwitchRow
import org.elnix.dragonlauncher.ui.dialogs.UserValidation
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import java.io.File

import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun LogsTab(
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var logs by remember { mutableStateOf("") }
    var selectedFile by remember { mutableStateOf<File?>(null) }

    var refreshTrigger by remember { mutableStateOf(0) }
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
    val packageInfo = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
    val versionName = packageInfo.versionName ?: "unknown"
    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode else packageInfo.versionCode.toLong()

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
                    // package may be under "package" or "packageName" in different registries
                    val pkgField = obj["package"] ?: obj["packageName"] ?: obj["id"]
                    val pkgValue = pkgField?.jsonPrimitive?.contentOrNull

                    // name may be a string or an object (localized); try multiple fallbacks
                    val nameField = obj["name"]
                    val labelValue = when {
                        nameField == null -> (obj["id"]?.jsonPrimitive?.contentOrNull ?: "Unknown")
                        nameField is JsonPrimitive && nameField.isString -> nameField.content
                        nameField is JsonObject -> (nameField["en"]?.jsonPrimitive?.contentOrNull
                            ?: nameField.values.firstOrNull()?.jsonPrimitive?.contentOrNull
                            ?: obj["id"]?.jsonPrimitive?.contentOrNull
                            ?: "Unknown")
                        else -> obj["id"]?.jsonPrimitive?.contentOrNull ?: "Unknown"
                    }

                    if (!pkgValue.isNullOrEmpty()) {
                        try {
                            if (ExtensionManager.isExtensionInstalled(ctx, pkgValue)) {
                                val pkgInfo = ctx.packageManager.getPackageInfo(pkgValue, 0)
                                val versionStr = pkgInfo.versionName ?: "unknown"
                                lines.add("$labelValue ($versionStr)")
                            }
                        } catch (_: Exception) {
                            // ignore missing packages
                        }
                    }
                } catch (_: Exception) {
                    // skip malformed entry
                }
            }
        }

        if (lines.isNotEmpty()) finalExtensionText = lines.joinToString("\n")
    } catch (_: Exception) {
        // registry not available or parse failed -> leave default text
    }

    val deviceDetails = remember {
        buildString {
            appendLine("--- DEVICE DETAILS ---")
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

            appendLine("\n--- EXTENSIONS ---")
            appendLine(finalExtensionText)

            appendLine("\n--- PERMISSIONS ---")
            try {
                val info = ctx.packageManager.getPackageInfo(ctx.packageName, PackageManager.GET_PERMISSIONS)
                info.requestedPermissions?.forEachIndexed { index, perm ->
                    val flags = info.requestedPermissionsFlags
                    val granted = (flags != null && (flags[index] and 0x00000002) != 0) ||
                            ContextCompat.checkSelfPermission(ctx, perm) == PackageManager.PERMISSION_GRANTED
                    appendLine("${perm.substringAfterLast(".")}: ${if (granted) "GRANTED" else "DENIED"}")
                }
            } catch (e: Exception) {
                appendLine("Error reading permissions: ${e.message}")
            }
        }
    }

    SettingsLazyHeader(
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
            ({
                refreshTrigger++
                ctx.showToast("Refreshing...")
            } to Icons.Default.Refresh)
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
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, "Copy Info", modifier = Modifier.size(20.dp))
                            }
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
                                        }
                                    ) {
                                        Icon(Icons.Default.Delete, "Delete")
                                    }

                                    DragonIconButton(
                                        onClick = {
                                            exportLogFile(ctx, file)
                                        }
                                    ) {
                                        Icon(Icons.Default.Share, "Export")
                                    }
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
                                        }
                                    ) {
                                        Icon(Icons.Default.ContentCopy, "Copy All")
                                    }

                                    DragonIconButton(
                                        onClick = {
                                            logs = ""
                                            selectedFile = null
                                        }
                                    ) {
                                        Icon(Icons.Default.Close, "Close")
                                    }
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
        ctx.logD {" Share opened: ${file.name} (${shareFile.absolutePath})" }

    } catch (e: SecurityException) {
        ctx.logE("LogsTab") { "FileProvider not configured: ${e.message}" }
        val content = DragonLogManager.readLogFile(file)
        val textIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content)
            putExtra(Intent.EXTRA_SUBJECT, "Dragon Logs - ${file.name}")
        }
        ctx.startActivity(Intent.createChooser(textIntent, "Share logs (text)"))
    } catch (e: Exception) {
        ctx.logE("LogsTab") { "Share failed: ${e.message}" }
    }
}
