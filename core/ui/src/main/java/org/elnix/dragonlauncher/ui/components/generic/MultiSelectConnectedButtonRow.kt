package org.elnix.dragonlauncher.ui.components.generic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.enumsui.ToggleButtonOption
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors

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
 * @param showLabel Whether to show the text label alongside the icon. Defaults to `true`.
 * @param hapticFeedback Whether to emit a [HapticFeedbackType.KeyboardTap] on every tap,
 *   regardless of the resulting checked state. Defaults to `true`.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T : ToggleButtonOption> MultiSelectConnectedButtonRow(
    entries: List<T>,

    modifier: Modifier = Modifier,

    // Optional parameters
    showLabel: Boolean = true,
    showLabelOnPress: Boolean = false,
    hapticFeedback: Boolean = true,

    isEnabled: (T) -> Boolean = { true },
    isChecked: (T) -> Boolean = { true },
    onCheck: (T) -> Unit
) {
    val haptic = LocalHapticFeedback.current


    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
    ) {
        entries.forEachIndexed { index, entry ->

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()


            // No idea why, but using a not here feels more natural for the displayed entries
            val checked = !isChecked(entry)

            ToggleButton(
                checked = checked,
                onCheckedChange = {
                    onCheck(entry)

                    if (hapticFeedback) {
                        haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                    }
                },
                interactionSource = interactionSource,
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

                // Animate the label on press, either always when asked, or only when pressed, using interactionSource
                AnimatedVisibility((showLabel || (isPressed && showLabelOnPress)) && entry.resId != null) {
                    Text(stringResource(entry.resId ?: -1))
                }
            }
        }
    }
}