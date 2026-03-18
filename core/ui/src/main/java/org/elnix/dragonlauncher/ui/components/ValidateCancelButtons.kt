package org.elnix.dragonlauncher.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors

@Composable
fun ValidateCancelButtons(
    validateText: String = stringResource(R.string.save),
    validateColor: Color = MaterialTheme.colorScheme.onPrimary,
    validateContainerColor: Color = MaterialTheme.colorScheme.primary,
    cancelText: String = stringResource(R.string.cancel),
    validateEnabled: Boolean = true,
    onCancel: (() -> Unit)? = null,
    onValidate: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (onCancel != null) {
            TextButton(
                onClick = onCancel,
                colors = AppObjectsColors.cancelButtonColors(),
                shape = CircleShape,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = cancelText)
            }
        }

        Button(
            enabled = validateEnabled,
            onClick = onValidate,
            colors = ButtonDefaults.buttonColors(
                contentColor = validateColor,
                containerColor = validateContainerColor
            ),
            modifier = Modifier.weight(1.5f),
            shape = CircleShape
        ) {
            Text(validateText)
        }

        Spacer(modifier = Modifier.width(10.dp))
    }
}
