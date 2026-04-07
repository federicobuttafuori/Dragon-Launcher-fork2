package org.elnix.dragonlauncher.ui.modifiers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.utils.colors.ColorUtils.semiTransparentIfDisabled
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape


@Composable
fun Modifier.settingsGroup(
    clickModifier: Modifier? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    border: Boolean = true,
    enabled: Boolean = true
): Modifier {
    return this
        .clip(DragonShape)
        .background(backgroundColor.semiTransparentIfDisabled(enabled))
        .conditional(border) {
            border(1.dp, MaterialTheme.colorScheme.outlineVariant.semiTransparentIfDisabled(enabled), DragonShape)
        }
        .then(clickModifier ?: this)
        .padding(10.dp)
}


@Composable
fun Modifier.buttonGroup(): Modifier = this
    .clip(CircleShape)
    .background(MaterialTheme.colorScheme.surface)
    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
    .padding(horizontal = 8.dp, vertical = 5.dp)