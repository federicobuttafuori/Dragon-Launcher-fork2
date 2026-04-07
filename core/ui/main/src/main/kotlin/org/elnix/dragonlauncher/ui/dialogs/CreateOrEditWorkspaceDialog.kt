package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.WorkspaceType
import org.elnix.dragonlauncher.ui.base.UiConstants.DragonShape
import org.elnix.dragonlauncher.theme.AppObjectsColors
import org.elnix.dragonlauncher.ui.dragon.components.ValidateCancelButtons
import org.elnix.dragonlauncher.ui.components.generic.ActionSelectorRow

@Composable
fun CreateOrEditWorkspaceDialog(
    visible: Boolean,
    title: String,
    name: String,
    type: WorkspaceType?,
    onNameChange: (String) -> Unit,
    onConfirm: (WorkspaceType) -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return

    var selectedType by remember { mutableStateOf(type ?: WorkspaceType.CUSTOM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            ValidateCancelButtons(
                onCancel = onDismiss,
                onConfirm = {
                    onConfirm(selectedType)
                }
            )
        },

        title = { Text(title) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ){
                TextField(
                    value = name,
                    onValueChange = onNameChange,
                    singleLine = true,
                    placeholder = {
                        Text(stringResource(R.string.workspace_name))
                    },
                    colors = AppObjectsColors.outlinedTextFieldColors(backgroundColor = MaterialTheme.colorScheme.surface, removeBorder = true)
                )

                ActionSelectorRow(
                    options = WorkspaceType.entries,
                    selected = selectedType,
                    switchEnabled = false,
                    label = stringResource(R.string.workspace_type)
                ) {
                    selectedType = it!!
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shape = DragonShape
    )
}
