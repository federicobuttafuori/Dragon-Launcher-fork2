package org.elnix.dragonlauncher.ui.dialogs

import android.content.pm.ShortcutInfo
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.base.theme.LocalExtraColors
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.utils.Constants
import org.elnix.dragonlauncher.common.utils.Constants.Actions.defaultChoosableActions
import org.elnix.dragonlauncher.common.utils.PackageManagerCompat
import org.elnix.dragonlauncher.common.utils.UiConstants.DragonShape
import org.elnix.dragonlauncher.settings.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.ui.actions.ActionIcon
import org.elnix.dragonlauncher.ui.actions.actionColor
import org.elnix.dragonlauncher.ui.actions.actionLabel
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.remembers.LocalAppsViewModel

@Suppress("AssignedValueIsNeverRead")
@Composable
fun AddPointDialog(
    actions: List<SwipeActionSerializable> = defaultChoosableActions,
    onNewNest: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    onActionSelected: (SwipeActionSerializable) -> Unit,
    onMultipleActionsSelected: ((List<SwipeActionSerializable>, Boolean) -> Unit)? = null
) {
    val ctx = LocalContext.current

    val appsViewModel = LocalAppsViewModel.current

    val pm = ctx.packageManager
    val packageManagerCompat = PackageManagerCompat(pm, ctx)

    var showAppPicker by remember { mutableStateOf(false) }
    var showUrlInput by remember { mutableStateOf(false) }
    var showFilePicker by remember { mutableStateOf(false) }
    var showNestPicker by remember { mutableStateOf(false) }
    var showWorkspacePicker by remember { mutableStateOf(false) }
    var showPinnedShortcutsPicker by remember { mutableStateOf(false) }
    var showSettingsPagePicker by remember { mutableStateOf(false) }

    val workspaces by appsViewModel.enabledState.collectAsState()

    val gridSize by DrawerSettingsStore.gridSize.asState()
    val showIcons by DrawerSettingsStore.showAppIconsInDrawer.asState()
    val showLabels by DrawerSettingsStore.showAppLabelInDrawer.asState()
    val promptForShortcuts by BehaviorSettingsStore.promptForShortcutsWhenAddingApp.asState()


    var selectedApp by remember { mutableStateOf<AppModel?>(null) }
    var shortcutDialogVisible by remember { mutableStateOf(false) }
    var shortcuts by remember { mutableStateOf<List<ShortcutInfo>>(emptyList()) }


    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = { Text(stringResource(R.string.choose_action)) },
        text = {
            LazyVerticalGrid(
                modifier = Modifier
                    .height(320.dp)
                    .clip(DragonShape),
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Loop through all actions
                items(actions) { action ->
                    when (action) {

                        // Open App → requires AppPicker
                        is SwipeActionSerializable.LaunchApp -> {
                            AddPointColumn(
                                action = action,
                                onSelected = { showAppPicker = true }
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        // Open URL → requires URL dialog
                        is SwipeActionSerializable.OpenUrl -> {
                            AddPointColumn(
                                action = action,
                                onSelected = { showUrlInput = true }
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        // Open File picker to choose a file
                        is SwipeActionSerializable.OpenFile -> {
                            AddPointColumn(
                                action = action,
                                onSelected = { showFilePicker = true }
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        // Open Circle Nest → requires nest picker
                        is SwipeActionSerializable.OpenCircleNest -> {
                            AddPointColumn(
                                action = action,
                                onSelected = { showNestPicker = true }
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        // Open App Drawer → workspace picker
                        is SwipeActionSerializable.OpenAppDrawer -> {
                            AddPointColumn(
                                action = action,
                                onSelected = { showWorkspacePicker = true }
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        // Open App Drawer → workspace picker
                        is SwipeActionSerializable.OpenDragonLauncherSettings -> {
                            AddPointColumn(
                                action = action,
                                onSelected = { showSettingsPagePicker = true }
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        // Pinned Shortcuts → browse all pinned shortcuts
                        is SwipeActionSerializable.LaunchShortcut -> {
                            if (action.packageName.isEmpty()) {
                                // Sentinel entry: open pinned shortcuts picker
                                AddPointColumn(
                                    action = action,
                                    onSelected = { showPinnedShortcutsPicker = true }
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }

                        // Direct actions
                        else -> {
                            AddPointColumn(
                                action = action,
                                onSelected = { onActionSelected(action) }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = DragonShape
    )

    if (showAppPicker) {
        AppPickerDialog(
            gridSize = gridSize,
            showIcons = showIcons,
            showLabels = showLabels,
            multiSelectEnabled = onMultipleActionsSelected != null,
            onDismiss = { showAppPicker = false },
            onAppSelected = { app ->

                ctx.logD(Constants.Logging.APP_LAUNCH_TAG) { "Selected App: $app" }

                // Try to query shortcuts, but handle crashes gracefully
                val list = if (promptForShortcuts) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            packageManagerCompat.queryAppShortcuts(app.packageName)
                        } else {
                            emptyList()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Some apps (Contacts, Gmail) may throw SecurityException or other errors
                        emptyList()
                    }
                } else {
                    emptyList() // Skip shortcut query if disabled
                }

                if (list.isNotEmpty()) {
                    selectedApp = app
                    shortcuts = list
                    shortcutDialogVisible = true
                } else {
                    onActionSelected(
                        SwipeActionSerializable.LaunchApp(
                            app.packageName,
                            app.isPrivateProfile,
                            app.userId ?: 0
                        )
                    )
                }
            },
            onMultipleAppsSelected = if (onMultipleActionsSelected != null) { { apps, autoPlace ->
                val actions = apps.map { SwipeActionSerializable.LaunchApp(it.packageName, it.isPrivateProfile, it.userId ?: 0) }
                onMultipleActionsSelected(actions, autoPlace)
                showAppPicker = false
            } } else null
        )
    }

    if (showUrlInput) {
        UrlInputDialog(
            onDismiss = { showUrlInput = false },
            onUrlSelected = {
                onActionSelected(it)
                showUrlInput = false
            }
        )
    }

    if (showFilePicker) {
        FilePickerDialog(
            onDismiss = { showFilePicker = false },
            onFileSelected = {
                onActionSelected(it)
                showFilePicker = false
            }
        )
    }

    if (shortcutDialogVisible && selectedApp != null) {
        AppShortcutPickerDialog(
            app = selectedApp!!,
            shortcuts = shortcuts,
            onDismiss = { shortcutDialogVisible = false },
            onShortcutSelected = {pkg, id ->
                onActionSelected(SwipeActionSerializable.LaunchShortcut(pkg, id))
                shortcutDialogVisible = false
            },
            onOpenApp = {
                onActionSelected(SwipeActionSerializable.LaunchApp(selectedApp!!.packageName, selectedApp!!.isPrivateProfile, selectedApp!!.userId ?: 0))
                onDismiss()
            }
        )
    }

    if (showNestPicker) {
        NestManagementDialog(
            onDismissRequest = { showNestPicker = false },
            title = stringResource(R.string.pick_a_nest),
            onNewNest = onNewNest,
            onNameChange = null,
            onDelete = null,
            onSelect = {
                onActionSelected(SwipeActionSerializable.OpenCircleNest(it.id))
                showNestPicker = false
            }
        )
    }

    if (showSettingsPagePicker) {
        SettingsPagePicker(
            onDismissRequest = { showSettingsPagePicker = false }
        ) {
            onActionSelected(SwipeActionSerializable.OpenDragonLauncherSettings(it))
            showSettingsPagePicker = false
        }
    }

    if (showWorkspacePicker) {
        val availableWorkspaces = workspaces.workspaces
        AlertDialog(
            onDismissRequest = { showWorkspacePicker = false },
            confirmButton = {},
            title = { Text(stringResource(R.string.select_default_workspace)) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = stringResource(R.string.select_workspace_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                    )
                    Spacer(Modifier.height(8.dp))

                    // "Default" option (no specific workspace)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(DragonShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                onActionSelected(SwipeActionSerializable.OpenAppDrawer())
                                showWorkspacePicker = false
                            }
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.workspace_default),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Specific workspaces
                    availableWorkspaces.forEach { workspace ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(DragonShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable {
                                    onActionSelected(
                                        SwipeActionSerializable.OpenAppDrawer(workspace.id)
                                    )
                                    showWorkspacePicker = false
                                }
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = workspace.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = DragonShape
        )
    }

    if (showPinnedShortcutsPicker) {
        PinnedShortcutsPickerDialog(
            onDismiss = { showPinnedShortcutsPicker = false },
            onShortcutSelected = { shortcutAction ->
                onActionSelected(shortcutAction)
                showPinnedShortcutsPicker = false
            }
        )
    }
}


@Composable
fun AddPointColumn(
    action: SwipeActionSerializable,
    onSelected: () -> Unit
) {
    val extraColors = LocalExtraColors.current

    val name = when(action) {
        is SwipeActionSerializable.LaunchApp -> stringResource(R.string.open_app)
        is SwipeActionSerializable.LaunchShortcut -> {
            if (action.packageName.isEmpty()) stringResource(R.string.pinned_shortcuts)
            else actionLabel(action)
        }
        is SwipeActionSerializable.OpenUrl -> stringResource(R.string.open_url)
        is SwipeActionSerializable.OpenFile -> stringResource(R.string.open_file)
        else -> actionLabel(action)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DragonShape)
            .background(actionColor(action, extraColors).copy(0.5f))
            .clickable { onSelected() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = name,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        ActionIcon(
            action = action,
            modifier = Modifier.size(30.dp),
            showLaunchAppVectorGrid = true
        )
    }
}
