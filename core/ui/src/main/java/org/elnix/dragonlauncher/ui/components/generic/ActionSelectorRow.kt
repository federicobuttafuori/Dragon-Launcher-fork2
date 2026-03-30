@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.components.generic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.utils.UiConstants.DragonShape
import org.elnix.dragonlauncher.common.utils.semiTransparentIfDisabled
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.dialogs.CustomAlertDialog
import org.elnix.dragonlauncher.ui.modifiers.conditional


@Composable
fun <T> ActionSelectorRow(
    options: List<T>,
    selected: T,
    enabled: Boolean = true,
    switchEnabled: Boolean = true,
    label: String,
    optionLabel: (T) -> String = { it.toString() },
    toggled: Boolean? = null,
    onSelected: (T?) -> Unit
) {
    val textColor = MaterialTheme.colorScheme.onSurface

    var showDialog by remember { mutableStateOf(false) }

    val baseModifier = Modifier.fillMaxWidth()

    val switchInteractionSource = remember { MutableInteractionSource() }
    val globalInteractionSource = remember { MutableInteractionSource() }


    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = baseModifier
            .clip(DragonShape)
            .height(IntrinsicSize.Max)
            .background(MaterialTheme.colorScheme.surface.semiTransparentIfDisabled(enabled))
            .conditional(enabled && toggled != true) {
                clickable(
                    interactionSource = switchInteractionSource
                ) { showDialog = true }
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .conditional(enabled && toggled == true) {
                    clickable(
                        interactionSource = globalInteractionSource
                    ) { showDialog = true }
                }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = label,
                color = textColor.semiTransparentIfDisabled(enabled),
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = optionLabel(selected),
                color = textColor.semiTransparentIfDisabled(enabled),
                style = MaterialTheme.typography.labelLarge
            )
        }

        // Right side toggle + divider wrapped in a clickable container
        if (toggled != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .conditional(enabled && toggled) {
                        clickable(
                            interactionSource = switchInteractionSource
                        ) {
                            // Disables, selects nothing
                            onSelected(null)
                        }
                    }
                    .fillMaxHeight()
                    .padding(top = 10.dp, bottom = 10.dp, end = 10.dp)
            ) {
                VerticalDivider(
                    modifier = Modifier
                        .height(50.dp)
                        .padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = toggled,
                    interactionSource = switchInteractionSource,
                    enabled = switchEnabled,
                    onCheckedChange = null,
                    colors = AppObjectsColors.switchColors(),
                )
            }
        }
    }

    // Options dialog
    ActionSelector(
        visible = showDialog,
        label = label,
        options = options,
        optionLabel = optionLabel,
        selected = selected,
        onSelected = onSelected,
        onDismiss = { showDialog = false }
    )
}


@Composable
fun <T> ActionSelector(
    visible: Boolean,
    label: String?,
    options: List<T>,
    optionLabel: (T) -> String = { it.toString() },
    selected: T?,
    onSelected: (T) -> Unit,
    onDismiss: () -> Unit,
) {
    val textColor = MaterialTheme.colorScheme.onSurface

    if (visible) {
        CustomAlertDialog(
            onDismissRequest = onDismiss,
            title = {
                if (label != null) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column {
                    options.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(DragonShape)
                                .clickable {
                                    onSelected(option)
                                    onDismiss()
                                }
                                .padding(15.dp)
                        ) {
                            Text(
                                text = optionLabel(option),
                                color = textColor,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )

                            RadioButton(
                                selected = (selected == option),
                                onClick = {
                                    onSelected(option)
                                    onDismiss()
                                },
                                colors = AppObjectsColors.radioButtonColors(),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}
