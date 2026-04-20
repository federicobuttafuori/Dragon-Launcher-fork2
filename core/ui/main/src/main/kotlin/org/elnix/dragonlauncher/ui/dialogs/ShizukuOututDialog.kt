package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.shizuku.OutputLine
import org.elnix.dragonlauncher.ui.dragon.dialogs.CustomAlertDialog
import org.elnix.dragonlauncher.ui.dragon.text.TextWithDescription

@Composable
fun ShizukuOutputDialog(
    output: OutputLine,
    onDismiss: () -> Unit
) {

    CustomAlertDialog(
        scroll = false,
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.command_output)) },
        text = {
            Column {
                val errorText = if (output.isError) "Error occurred" else null

                TextWithDescription(
                    text = output.text,
                    description = errorText
                )
            }
        }
    )
}
