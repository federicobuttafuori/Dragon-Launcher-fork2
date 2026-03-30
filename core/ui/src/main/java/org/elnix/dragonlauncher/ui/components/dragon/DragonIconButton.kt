package org.elnix.dragonlauncher.ui.components.dragon

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import org.elnix.dragonlauncher.common.utils.UiConstants
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors


@Composable
fun DragonIconButtonImpl(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors,
    content: @Composable () -> Unit,
) {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shapeRound by animateDpAsState(
        targetValue = if (isPressed)
            UiConstants.DRAGON_SHAPE_CORNER_DP
        else
            UiConstants.CIRCLE_SHAPE_CORNER_DP,
        label = "shape_anim"
    )

    val shape = RoundedCornerShape(shapeRound)

    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        shape = shape,
        content = content
    )
}

@Composable
fun DragonIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = AppObjectsColors.iconButtonColors(),
    imageVector: ImageVector,
    contentDescription: String
) {

    DragonTooltip(contentDescription) {
        DragonIconButtonImpl(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            colors = colors
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription
            )
        }
    }
}

