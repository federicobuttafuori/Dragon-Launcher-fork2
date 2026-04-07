package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.components.ValidateCancelButtons

@Composable
fun AdbCommandInputDialog(
    onDismiss: () -> Unit,
    onUrlSelected: (SwipeActionSerializable.RunAdbCommand) -> Unit
) {
    var text by remember { mutableStateOf("adb ") }

    CustomAlertDialog(
        scroll = false,
        alignment = Alignment.Center,
        modifier = Modifier.padding(40.dp),
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.enter_url)) },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        text = it
                    },
                    singleLine = true,
                    label = { Text(stringResource(R.string.adb_command_without_adb)) },
                    colors = AppObjectsColors.outlinedTextFieldColors()
                )
            }
        },
        confirmButton = {
            ValidateCancelButtons(
                onCancel = onDismiss
            ) {
                onUrlSelected(SwipeActionSerializable.RunAdbCommand(text))
                onDismiss()
            }
        }
    )
}
