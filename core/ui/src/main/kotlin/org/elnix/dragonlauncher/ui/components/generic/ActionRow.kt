package org.elnix.dragonlauncher.ui.components.generic

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.elnix.dragonlauncher.common.utils.semiTransparentIfDisabled
import org.elnix.dragonlauncher.ui.UiConstants
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.helpers.withHaptic

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> ActionRow(
    actions: List<T>,
    selectedView: T?,
    enabled: Boolean = true,
    backgroundColorUnselected: Color? = null,
    actionName: @Composable ((T) -> String)? = null,
    actionIcon: @Composable ((T) -> ImageVector)? = null,
    onClick: (T) -> Unit
) {
    val interactionSources = remember { List(actions.size) { MutableInteractionSource() } }

    @Suppress("DEPRECATION")
    ButtonGroup(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
    ) {
        actions.forEachIndexed { index, mode ->
            val isSelected = mode == selectedView

            val backgroundColor = (
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else backgroundColorUnselected ?: MaterialTheme.colorScheme.surface
                    ).semiTransparentIfDisabled(enabled)

            val textColor = (
                    if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface
                    ).semiTransparentIfDisabled(enabled)

            Button(
                onClick = withHaptic { onClick(mode) },
                shapes = UiConstants.dragonShapes(),
                modifier = Modifier
                    .weight(1f)
                    .animateWidth(interactionSources[index]),
                interactionSource = interactionSources[index],
                colors = AppObjectsColors.buttonColors(backgroundColor)
            ) {
                actionIcon?.let {
                    Icon(
                        imageVector = it(mode),
                        contentDescription = null,
                        tint = textColor
                    )
                    Spacer(Modifier.width(5.dp))
                }

                actionName?.let {
                    Text(
                        text = it(mode),
                        modifier = Modifier
                            .padding(12.dp),
                        color = textColor,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
