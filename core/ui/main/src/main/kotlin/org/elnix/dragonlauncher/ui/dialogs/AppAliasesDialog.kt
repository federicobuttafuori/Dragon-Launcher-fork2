@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.common.serializables.WorkspaceState
import org.elnix.dragonlauncher.ui.base.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.dragon.components.ValidateCancelButtons
import org.elnix.dragonlauncher.ui.dragon.components.DragonIconButton
import org.elnix.dragonlauncher.ui.helpers.Bubble
import org.elnix.dragonlauncher.ui.composition.LocalAppsViewModel

@Composable
fun AppAliasesDialog(
    app: AppModel,
    onDismiss: () -> Unit
) {
    val appsViewModel = LocalAppsViewModel.current

    var showAliasEditScreen by remember { mutableStateOf<String?>(null) }
    val cacheKey = app.iconCacheKey

    val state by appsViewModel.enabledState
        .collectAsState(WorkspaceState())

    @Suppress("UselessCallOnNotNull")
    val aliases = state.appAliases.orEmpty()


    AlertDialog(
        title = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.AlternateEmail,
                        contentDescription = stringResource(R.string.app_aliases),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = stringResource(R.string.app_aliases),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                }

                Text(
                    text = app.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        onDismissRequest = onDismiss,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 700.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FlowRow(
                    verticalArrangement = Arrangement.Center,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    DragonIconButton(
                        onClick = { showAliasEditScreen = "" },
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_alias)
                    )

                    aliases[cacheKey]?.forEach { alias ->

                        Bubble(
                            onClick = { showAliasEditScreen = alias },
                            onLongClick = {
                                appsViewModel.removeAliasFromWorkspace(alias, cacheKey)
                            }
                        ) {
                            Text(
                                text = alias,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            ValidateCancelButtons(
                validateText = stringResource(R.string.ok),
                onConfirm = onDismiss
            )
        },
        dismissButton = {},
        containerColor = MaterialTheme.colorScheme.surface,
        shape = DragonShape
    )

    if (showAliasEditScreen != null) {

        val aliasToEdit = showAliasEditScreen!!

        EditAliasDialog(
            initialAlias = aliasToEdit,
            onDismiss = { showAliasEditScreen = null }
        ) {
            appsViewModel.removeAliasFromWorkspace(aliasToEdit, cacheKey)
            appsViewModel.addAliasToApp(it, cacheKey)
            showAliasEditScreen = null
        }
    }
}
