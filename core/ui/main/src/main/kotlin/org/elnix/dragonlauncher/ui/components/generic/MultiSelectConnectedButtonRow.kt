package org.elnix.dragonlauncher.ui.components.generic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.enumsui.ToggleButtonOption
import org.elnix.dragonlauncher.ui.base.withHapticParam
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.components.dragon.DragonTooltip


enum class ShowLabels {
    Always,
    Selected,
    Never
}

/**
 * A horizontally connected multi-select toggle button group built on Material3 Expressive's
 * [ButtonGroupDefaults] connected shape system.
 *
 * Each button independently toggles without affecting the others, making this suitable
 * for multi-select filter rows, editor toolbars, or any set of orthogonal on/off options.
 *
 * Connected shapes are applied automatically based on position:
 * - First entry → [ButtonGroupDefaults.connectedLeadingButtonShapes]
 * - Last entry  → [ButtonGroupDefaults.connectedTrailingButtonShapes]
 * - Middle entries → [ButtonGroupDefaults.connectedMiddleButtonShapes]
 *
 * @param T Any type implementing [ToggleButtonOption], typically an enum.
 * @param entries The ordered list of options to display as toggle buttons.
 * @param isChecked Predicate returning the current checked state for a given entry.
 * @param onCheck Called when the user taps a button, both on check and uncheck.
 * @param showLabels Whether to show the text label alongside the icon. Defaults to `true`.
 *   regardless of the resulting checked state. Defaults to `true`.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T : ToggleButtonOption> MultiSelectConnectedButtonRow(
    entries: List<T>,
    modifier: Modifier = Modifier,
    showLabels: ShowLabels = ShowLabels.Never,
    isEnabled: (T) -> Boolean = { true },
    isChecked: (T) -> Boolean = { true },
    onCheck: (T) -> Unit
) {

    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
    ) {
        entries.forEachIndexed { index, entry ->

            // No idea why, but using a `not` here feels more natural for the displayed entries
            val checked = !isChecked(entry)

            val showLabel = (showLabels == ShowLabels.Always) || (showLabels == ShowLabels.Selected && !checked)

            @OptIn(ExperimentalMaterial3Api::class)
            DragonTooltip(
                resId = entry.resId ?: -1,
                enabled = !showLabel
            ) {
                ToggleButton(
                    checked = checked,
                    onCheckedChange = withHapticParam {
                        onCheck(entry)
                    },
                    enabled = isEnabled(entry),
                    colors = AppObjectsColors.toggleButtonColors(),
                    // Custom shapes
                    shapes = when (index) {
                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                        entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                    }
                ) {
                    Crossfade(!checked) { notChecked ->
                        Icon(
                            entry.iconDisabled.takeIf { notChecked && it != null } ?: entry.iconEnabled,
                            contentDescription = null
                        )
                    }


                    AnimatedVisibility(showLabel) {
                        entry.resId?.let{
                            Spacer(Modifier.width(5.dp))
                            Text(
                                stringResource(it),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }
    }
}