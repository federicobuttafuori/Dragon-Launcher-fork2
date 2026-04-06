@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.logging.logD
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.common.utils.Constants.Logging.APPS_TAG
import org.elnix.dragonlauncher.common.utils.resolveShape
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.actions.appIcon
import org.elnix.dragonlauncher.ui.components.Spacer
import org.elnix.dragonlauncher.ui.components.dragon.DragonDropDownMenu
import org.elnix.dragonlauncher.ui.components.dragon.DragonIconButton
import org.elnix.dragonlauncher.ui.remembers.LocalIconShape

private data class DialogEntry(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun AppLongPressPopup(
    expanded: () -> Boolean,
    app: AppModel,
    // Normal
    onOpen: (() -> Unit)? = null,
    onSettings: (() -> Unit)? = null,
    onUninstall: (() -> Unit)? = null,
    // Workspace
    onRemoveFromWorkspace: (() -> Unit)? = null,
    onAddToWorkspace: (() -> Unit)? = null,
    onRenameApp: (() -> Unit)? = null,
    onChangeAppIcon: (() -> Unit)? = null,
    onAliases: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {

    val iconsShape = LocalIconShape.current

    var showDetailedAppInfoDialog by remember { mutableStateOf(false) }

    val entries = buildList {
        onOpen?.let {
            add(
                DialogEntry(
                    label = stringResource(R.string.open),
                    icon = Icons.AutoMirrored.Filled.OpenInNew,
                    onClick = { onDismiss(); it() }
                )
            )
        }


        onUninstall?.let {
            add(
                DialogEntry(
                    label = stringResource(R.string.uninstall),
                    icon = Icons.Default.Delete,
                    onClick = { onDismiss(); it() }
                )
            )
        }

        onAliases?.let {
            add(
                DialogEntry(
                    label = stringResource(R.string.app_aliases),
                    icon = Icons.Default.AlternateEmail,
                    onClick = { onDismiss(); it() }
                )
            )
        }

        onRenameApp?.let {
            add(
                DialogEntry(
                    label = stringResource(R.string.rename_app),
                    icon = Icons.Default.Edit,
                    onClick = { onDismiss(); it() }
                )
            )
        }

        onChangeAppIcon?.let {
            add(
                DialogEntry(
                    label = stringResource(R.string.change_app_icon),
                    icon = Icons.Default.Image,
                    onClick = { onDismiss(); it() }
                )
            )
        }

        onAddToWorkspace?.let {
            add(
                DialogEntry(
                    label = stringResource(R.string.add_to_workspace),
                    icon = Icons.Default.Add,
                    onClick = { onDismiss(); it() }
                )
            )
        }

        onRemoveFromWorkspace?.let {
            add(
                DialogEntry(
                    label = stringResource(R.string.remove_from_workspace),
                    icon = Icons.Default.Close,
                    onClick = { onDismiss(); it() }
                )
            )
        }
    }

    @Composable
    fun TitleRow() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(app.name)

            Image(
                painter = appIcon(app),
                contentDescription = "App icon",
                modifier = Modifier
                    .size(32.dp)
                    .clip(iconsShape.resolveShape())
            )

            Spacer()

            onSettings?.let {
                DragonIconButton(
                    onClick = it,
                    imageVector = Icons.Default.Settings,
                    contentDescription = "App info",
                )
            }


            DragonIconButton(
                onClick = { showDetailedAppInfoDialog = true },
                imageVector = Icons.Default.Info,
                contentDescription = "Details",
            )
        }
    }


    @Composable
    fun EntryButton(
        entry: DialogEntry,
        modifier: Modifier = Modifier
    ) {

        Column(
            modifier = modifier
                .clip(DragonShape)
                .clickable { entry.onClick() }
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = entry.icon,
                contentDescription = entry.label,
                modifier = Modifier.size(30.dp)
            )

            Spacer(12.dp)

            Text(
                text = entry.label,
                fontSize = 10.sp,
                softWrap = false,
                overflow = TextOverflow.Visible,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }


    logD(APPS_TAG) { "Showing ${entries.size}" }


    var showOtherOptions by remember { mutableStateOf(false) }


    DragonDropDownMenu(
        expanded = expanded(),
        onDismissRequest = onDismiss
    ) {

        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clip(DragonShape)
                    .background(MaterialTheme.colorScheme.surface)

            ) {

                val mainEntries = entries.take(5)
                val otherEntries = if (entries.size > 5) {
                    entries.drop(5)
                } else null

                logD(APPS_TAG) { "Main entries size: ${mainEntries.size}" }
                logD(APPS_TAG) { "Other entries size: ${otherEntries?.size}" }

                Row {
                    mainEntries.forEach {
                        EntryButton(it, Modifier.weight(1f))
                    }

                    EntryButton(
                        DialogEntry(
                            label = if (showOtherOptions) stringResource(R.string.less) else stringResource(R.string.more),
                            icon = Icons.Default.MoreVert,
                            onClick = { showOtherOptions = !showOtherOptions }
                        )
                    )
                }

                AnimatedVisibility(showOtherOptions) {
                    Row {
                        otherEntries?.forEach {
                            EntryButton(it, Modifier.weight(1f))
                        }
                    }
                }

                TitleRow()
            }
        }
    }


    if (showDetailedAppInfoDialog) {
        AppModelInfoDialog(app) { showDetailedAppInfoDialog = false }
    }
}
