package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.enumsui.PrivateSpaceLoadingState
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape


@Composable
fun PrivateSpaceStateDebugScreen(
    state: PrivateSpaceLoadingState
) {
    Column(
        modifier = Modifier
            .clip(DragonShape)
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
    ) {
        DebugBooleanRow("isLocked", state.isLocked)
        DebugBooleanRow("isLoading", state.isLoading)
        DebugBooleanRow("isAuthenticating", state.isAuthenticating)
    }
}

@Composable
private fun DebugBooleanRow(
    label: String,
    value: Boolean
) {
    val valueColor = if (value) Color(0xFF2ECC71) else Color(0xFFE74C3C)

    Text(
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onBackground
                )
            ) {
                append(label)
                append(": ")
            }

            withStyle(
                style = SpanStyle(
                    color = valueColor,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(value.toString())
            }
        },
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(8.dp)
    )
}
