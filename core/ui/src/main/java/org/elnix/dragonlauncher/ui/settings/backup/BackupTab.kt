package org.elnix.dragonlauncher.ui.settings.backup

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.utils.UiConstants.DragonShape
import org.elnix.dragonlauncher.common.utils.formatDateTime
import org.elnix.dragonlauncher.common.utils.getFilePathFromUri
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.enumsui.BackupActions
import org.elnix.dragonlauncher.enumsui.label
import org.elnix.dragonlauncher.models.BackupResult
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.SettingsBackupManager
import org.elnix.dragonlauncher.settings.backupableStores
import org.elnix.dragonlauncher.settings.stores.BackupSettingsStore
import org.elnix.dragonlauncher.settings.stores.PrivateSettingsStore
import org.elnix.dragonlauncher.ui.components.TextDivider
import org.elnix.dragonlauncher.ui.components.generic.ActionRow
import org.elnix.dragonlauncher.ui.dialogs.ExportSettingsDialog
import org.elnix.dragonlauncher.ui.dialogs.ImportSettingsDialog
import org.elnix.dragonlauncher.ui.helpers.GradientBigButton
import org.elnix.dragonlauncher.ui.helpers.SwitchRow
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
                ctx.logE("Backup") { "Persistable permission not available for URI: $uri" }
            }
        }
    }


    // ───────────────────────────────────────
    // UI
    // ───────────────────────────────────────
    SettingsLazyHeader(
        title = ctx.getString(R.string.backup_restore),
        onBack = onBack,
        helpText = ctx.getString(R.string.backup_restore_text),
        resetText = null,
        onReset = null
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
            SwitchRow(
                state = autoBackupEnabled,
                text = ctx.getString(R.string.automatic_backups)
            ) { enabled ->
                scope.launch {
                    BackupSettingsStore.autoBackupEnabled.set(ctx, enabled)
                }
            }
        }

        if (autoBackupEnabled) {
            if (backupPath != null) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Backup Path: ",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = backupPath,
                            color = MaterialTheme.colorScheme.primary,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier
                                .clickable {
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
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            item {

                if (backupPath != null) {

                    ActionRow(
                        actions = BackupActions.entries,
                        selectedView = null,
                        actionName = { it.label(ctx) }
                    ) {
                        when (it) {
                            BackupActions.CHANGE -> {
                                autoBackupLauncher.launch("dragonlauncher-auto-backup.json")
                            }

                            BackupActions.REMOVE -> {
                                scope.launch {
                                    BackupSettingsStore.autoBackupUri.reset(ctx)
                                    BackupSettingsStore.autoBackupEnabled.reset(ctx)
                                }
                            }

                            BackupActions.TRIGGER -> {
                                scope.launch {
                                    SettingsBackupManager.triggerBackup(ctx)
                                }
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(0.5f))
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable {
                                    autoBackupLauncher.launch("dragonlauncher-auto-backup.json")
                                },

                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ctx.getString(R.string.select_backup_file),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }


            if (backupPath != null) {
                item {
                    Text(
                        text = "${ctx.getString(R.string.last_backup)} : ${lastBackupTime.formatDateTime()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }



            item { TextDivider(ctx.getString(R.string.auto_backup_stores)) }

            item {
                Column{
                    backupableStores.forEach { entry ->
                        val dataStoreName = entry.key
                        val settingsStore = entry.value

                        val isSelected = backupStores.contains(dataStoreName.value)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(DragonShape)
                                .clickable {
                                    scope.launch {
                                        val updated = if (isSelected) {
                                            backupStores - dataStoreName.value
                                        } else {
                                            backupStores + dataStoreName.value
                                        }
                                        BackupSettingsStore.backupStores.set(ctx, updated)
                                    }
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = settingsStore.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null
                            )
                        }
                    }
                }
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
