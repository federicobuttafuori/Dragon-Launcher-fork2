package org.elnix.dragonlauncher.ui.components.dragon

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.utils.UiConstants.DragonShape
import org.elnix.dragonlauncher.common.utils.semiTransparentIfDisabled
import org.elnix.dragonlauncher.ui.modifiers.shapedClickable


@Composable
fun DragonSurfaceRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape? = DragonShape,
    shapedCLickable: Boolean = false,
    onLongClick: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable RowScope.() -> Unit
) {
    // If you specify the shape, you cannot be shapedClickable
    require((shape != null) xor (shapedCLickable))

    Row(
        modifier = modifier
            .then(
                if (shapedCLickable) {
                    Modifier.shapedClickable(
                        enabled = enabled,
                        onLongClick = onLongClick,
                        onClick = onClick
                    )
                } else {
                    Modifier
                        .clip(shape!!)
                        .combinedClickable(
                        enabled = enabled,
                        onLongClick = onLongClick,
                        onClick = onClick
                    )
                }
            )
            .background(backgroundColor.semiTransparentIfDisabled(enabled))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}



@Composable
fun DragonSurfaceRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .clip(DragonShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}