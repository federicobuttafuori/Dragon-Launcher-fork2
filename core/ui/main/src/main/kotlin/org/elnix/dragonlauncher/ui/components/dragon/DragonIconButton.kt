package org.elnix.dragonlauncher.ui.components.dragon

import androidx.compose.animation.Crossfade
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import org.elnix.dragonlauncher.ui.UiConstants
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DragonIconButtonImpl(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors,
    content: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        shapes = UiConstants.dragonIconButtonShapes(),
        content = content
    )
}

@Composable
fun DragonIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: () -> Boolean = { true },
    colors: IconButtonColors = AppObjectsColors.iconButtonColors(),
    imageVector: ImageVector,
    contentDescription: String
) {

    DragonTooltip(contentDescription) {
        DragonIconButtonImpl(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled(),
            colors = colors
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription
            )
        }
    }
}


@Composable
fun ToggleableDragonIconButton(
    onClick: () -> Unit,
    toggled: () -> Boolean,
    modifier: Modifier = Modifier,
    enabled: () -> Boolean = { true },
    colors: IconButtonColors = AppObjectsColors.iconButtonColors(),
    imageVectorEnabled: ImageVector,
    imageVectorDisabled: ImageVector,
    contentDescription: String
) {

    DragonTooltip(contentDescription) {
        DragonIconButtonImpl(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled(),
            colors = colors
        ) {
            Crossfade(toggled()) {
                if (it) {
                    Icon(
                        imageVector = imageVectorEnabled,
                        contentDescription = contentDescription
                    )
                } else {
                    Icon(
                        imageVector = imageVectorDisabled,
                        contentDescription = contentDescription
                    )
                }
            }
        }
    }
}
