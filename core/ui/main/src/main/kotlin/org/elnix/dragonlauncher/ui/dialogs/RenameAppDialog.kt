package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors

@Composable
fun RenameAppDialog(
    title: String,
    name: () -> String,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = AppObjectsColors.buttonColors()
            ) {
                Text(
                    text = stringResource(R.string.ok),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = AppObjectsColors.cancelButtonColors()
            ) {
                Text(
                    stringResource(R.string.cancel),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        title = { Text(title) },
        text = {
            Column{
                TextField(
                    value = name(),
                    onValueChange = onNameChange,
                    singleLine = true,
                    colors = AppObjectsColors.outlinedTextFieldColors(
                        backgroundColor = MaterialTheme.colorScheme.surface
                    )
                )

                Button(
                    onClick = onReset,
                    colors = AppObjectsColors.buttonColors()
                ) {
                    Text(stringResource(R.string.reset))
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shape = DragonShape
    )
}
