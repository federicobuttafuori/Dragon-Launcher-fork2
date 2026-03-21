package org.elnix.dragonlauncher.ui.settings.backup

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.utils.Constants.Logging.BACKUP_TAG
import org.elnix.dragonlauncher.common.utils.formatDateTime
import org.elnix.dragonlauncher.common.utils.getFilePathFromUri
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.models.BackupResult
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.SettingsBackupManager
import org.elnix.dragonlauncher.settings.backupableStores
import org.elnix.dragonlauncher.settings.stores.BackupSettingsStore
import org.elnix.dragonlauncher.settings.stores.PrivateSettingsStore
import org.elnix.dragonlauncher.ui.components.TextDivider
import org.elnix.dragonlauncher.ui.components.dragon.DragonButton
import org.elnix.dragonlauncher.ui.components.dragon.DragonSurfaceRow
import org.elnix.dragonlauncher.ui.components.settings.SettingsSwitchRow
import org.elnix.dragonlauncher.ui.dialogs.ExportSettingsDialog
import org.elnix.dragonlauncher.ui.dialogs.ImportSettingsDialog
import org.elnix.dragonlauncher.ui.helpers.GradientBigButton
import org.elnix.dragonlauncher.ui.helpers.settings.SettingItemWithExternalOpen
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsItem
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.ui.remembers.LocalBackupViewModel
import org.elnix.dragonlauncher.ui.remembers.rememberSettingsImportLauncher
import org.json.JSONObject

@SuppressLint("LocalContextGetResourceValueCall")
@Suppress("AssignedValueIsNeverRead")
@Composable
fun BackupTab(onBack: () -> Unit) {
    val ctx = LocalContext.current
    val backupViewModel = LocalBackupViewModel.current

    val scope = rememberCoroutineScope()

    val autoBackupEnabled by BackupSettingsStore.autoBackupEnabled.flow(ctx).collectAsState(initial = false)
    val autoBackupUriString by BackupSettingsStore.autoBackupUri.flow(ctx).collectAsState(initial = null)
    val lastBackupTime by PrivateSettingsStore.lastBackupTime.flow(ctx).collectAsState(initial = 0L)

    val backupStores by BackupSettingsStore.backupStores.flow(ctx).collectAsState(initial = emptySet())

    LaunchedEffect(lastBackupTime) {
        ctx.showToast(lastBackupTime)
    }

    val autoBackupUri: Uri? = autoBackupUriString?.takeIf { it.isNotEmpty() }?.toUri()

    val backupPath: String? = autoBackupUri?.let { uri ->
        getFilePathFromUri(ctx, uri)
    }

    var selectedStoresForExport by remember { mutableStateOf(setOf<DataStoreName>()) }
    var selectedStoresForImport by remember { mutableStateOf(setOf<DataStoreName>()) }
    var importJson by remember { mutableStateOf<JSONObject?>(null) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    // ───────────────────────────────────────
    // SETTINGS EXPORT LAUNCHER
    // ───────────────────────────────────────
    val settingsExportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            if (uri == null) {
                backupViewModel.setResult(
                    BackupResult(
                        export = true,
                        error = true,
                        title = ctx.getString(R.string.export_cancelled)
                    )
                )
                return@rememberLauncherForActivityResult
            }

            scope.launch {
                try {
                    SettingsBackupManager.exportSettings(ctx, uri, selectedStoresForExport)
                    backupViewModel.setResult(
                        BackupResult(
                            export = true,
                            error = false,
                            title = ctx.getString(R.string.export_successful)
                        )
                    )
                } catch (e: Exception) {
                    backupViewModel.setResult(
                        BackupResult(
                            export = true,
                            error = true,
                            title = ctx.getString(R.string.export_failed),
                            message = e.message ?: ""
                        )
                    )
                }
            }
        }


    val settingsImportLauncher = rememberSettingsImportLauncher(
        ctx = ctx,
        scope = scope,
        onCancel = {
            backupViewModel.setResult(
                BackupResult(
                    export = false,
                    error = true,
                    title = ctx.getString(R.string.import_cancelled)
                )
            )
        },
        onError = { msg ->
            backupViewModel.setResult(
                BackupResult(
                    export = false,
                    error = true,
                    title = ctx.getString(R.string.import_failed),
                    message = msg
                )
            )
        },
        onJsonReady = { json ->
            importJson = json
            showImportDialog = true
        }
    )


    val autoBackupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            try {
                ctx.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                // Proceed only if successful
                scope.launch {
                    BackupSettingsStore.autoBackupUri.set(ctx, uri.toString())
                    BackupSettingsStore.autoBackupEnabled.set(ctx, true)
                }
                backupViewModel.setResult(
                    BackupResult(
                        export = true,
                        error = false,
                        title = "Auto-backup enabled"
                    )
                )
            } catch (e: SecurityException) {
                // Fallback: Store non-persistable URI or notify user
                backupViewModel.setResult(
                    BackupResult(
                        export = true,
                        error = true,
                        title = "Backup saved (limited persistence)"
                    )
                )
                logE(BACKUP_TAG, e) { "Persistable permission not available for URI: $uri" }
            }
        }
    }


    // ──────── UI ───────────────────────────────────────
    SettingsLazyHeader(
        title = ctx.getString(R.string.backup_restore),
        onBack = onBack,
        helpText = ctx.getString(R.string.backup_restore_text),
        onReset = {
            scope.launch {
                BackupSettingsStore.resetAll(ctx)
            }
        }
    ) {
        item {
            BackupButtons(
                onExport = { showExportDialog = true },
                onImport = {
                    settingsImportLauncher.launch(
                        arrayOf(
                            "application/json",
                            "text/plain",
                            "application/octet-stream",
                            "*/*"
                        )
                    )
                }
            )
        }

        item { TextDivider(ctx.getString(R.string.automatic_backups)) }

        item {
            SettingsSwitchRow(
                setting = BackupSettingsStore.autoBackupEnabled,
                title = stringResource(R.string.automatic_backups),
                description = stringResource(R.string.auto_backup_desc)
            ) {
                // If the user disabled the backup, also remove the uri
                if (!it) {
                    scope.launch {
                        BackupSettingsStore.autoBackupUri.reset(ctx)
                    }
                }
            }
        }

        if (autoBackupEnabled) {
            item {
                AnimatedContent(backupPath == null) { state ->
                    if (state) {
                        SettingsItem(
                            title = stringResource(R.string.backup_location),
                            description = stringResource(R.string.backup_location_desc),
                            icon = Icons.Default.FolderOpen,
                            onClick = { autoBackupLauncher.launch("dragonlauncher-auto-backup.json") }
                        )
                    } else {
                        SettingItemWithExternalOpen(
                            title = stringResource(R.string.backup_location),
                            description = backupPath
                                ?: stringResource(R.string.backup_location_desc),
                            icon = Icons.Default.FolderOpen,
                            onClick = {
                                autoBackupLauncher.launch("dragonlauncher-auto-backup.json")
                            },
                            onExtClick = {
                                autoBackupUri.let { uri ->
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(uri, "application/json")
                                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    }
                                    ctx.startActivity(
                                        Intent.createChooser(
                                            intent,
                                            "Open backup file"
                                        )
                                    )
                                }
                            }
                        )
                    }
                }
            }

            if (backupPath != null) {
                item {
                    SettingsItem(
                        title = stringResource(R.string.last_backup),
                        description = lastBackupTime.formatDateTime(),
                        icon = Icons.Default.Restore,
                        onClick = {
                            scope.launch {
                                SettingsBackupManager.triggerBackup(ctx)
                                ctx.showToast(ctx.getString(R.string.backup_triggered))
                            }
                        }
                    )
                }

                item {
                    SettingsItem(
                        title = stringResource(R.string.clear_auto_backup),
                        description = stringResource(R.string.clear_auto_backup_desc),
                        icon = Icons.Default.Close,
                        onClick = {
                            scope.launch {
                                BackupSettingsStore.autoBackupUri.reset(ctx)
                                BackupSettingsStore.autoBackupEnabled.reset(ctx)
                                ctx.showToast(ctx.getString(R.string.auto_backup_cleared))
                            }
                        }
                    )
                }
            }
        }

        item { TextDivider(ctx.getString(R.string.auto_backup_stores)) }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DragonButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            scope.launch {
                                BackupSettingsStore.backupStores.set(ctx, emptySet())
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Deselect,
                            contentDescription = stringResource(R.string.deselect_all)
                        )
                        Text(stringResource(R.string.deselect_all))
                    }


                    DragonButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            scope.launch {
                                BackupSettingsStore.backupStores.reset(ctx)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.SelectAll,
                            contentDescription = stringResource(R.string.select_all)
                        )
                        Text(stringResource(R.string.select_all))
                    }
                }
            }

        }

        items(backupableStores.entries.toList()) { entry ->
            val dataStoreName = entry.key
            val settingsStore = entry.value
            val isSelected = backupStores.contains(dataStoreName.value)

            DragonSurfaceRow(
                onClick = {
                    scope.launch {
                        val updated = if (isSelected) backupStores - dataStoreName.value
                        else backupStores + dataStoreName.value
                        BackupSettingsStore.backupStores.set(ctx, updated)
                    }
                }
            ) {
                Text(
                    text = settingsStore.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Checkbox(checked = isSelected, onCheckedChange = null)
            }
        }
    }

    // Export Dialog
    if (showExportDialog) {
        ExportSettingsDialog(
            onDismiss = { showExportDialog = false },
            onConfirm = { selectedStores ->
                showExportDialog = false
                selectedStoresForExport = selectedStores.keys
                settingsExportLauncher.launch("backup-${System.currentTimeMillis()}.json")
            }
        )
    }

    // Import Dialog (shows after file is picked)
    importJson?.let { json ->
        if (showImportDialog) {
            ImportSettingsDialog(
                backupJson = json,
                onDismiss = {
                    showImportDialog = false
                    importJson = null
                },
                onConfirm = { selectedStores ->
                    showImportDialog = false
                    selectedStoresForImport = selectedStores.keys

                    scope.launch {
                        try {
                            SettingsBackupManager.importSettingsFromJson(
                                ctx,
                                json,
                                selectedStoresForImport
                            )
                            backupViewModel.setResult(
                                BackupResult(
                                    export = false,
                                    error = false,
                                    title = ctx.getString(R.string.import_successful)
                                )
                            )
                            importJson = null
                        } catch (e: Exception) {
                            logE(BACKUP_TAG, e) { "Import failed" }
                            backupViewModel.setResult(
                                BackupResult(
                                    export = false,
                                    error = true,
                                    title = ctx.getString(R.string.import_failed),
                                    message = e.message ?: ""
                                )
                            )
                        }
                    }
                }
            )
        }
    }
}


// ───────────────────────────────────────
// Shared Buttons (internal)
// ───────────────────────────────────────
@Composable
private fun BackupButtons(
    onExport: () -> Unit,
    onImport: () -> Unit
) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        GradientBigButton(
            text = stringResource(R.string.export_settings),
            onClick = onExport,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = stringResource(R.string.export_settings),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier
        )

        GradientBigButton(
            text = stringResource(R.string.import_settings),
            onClick = onImport,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = stringResource(R.string.import_settings),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier
        )
    }
}
