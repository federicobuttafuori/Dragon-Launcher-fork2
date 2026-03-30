@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.colors

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.utils.UiConstants.DragonShape
import org.elnix.dragonlauncher.common.utils.colors.blendWith
import org.elnix.dragonlauncher.common.utils.colors.toHexWithAlpha
import org.elnix.dragonlauncher.common.utils.copyToClipboard
import org.elnix.dragonlauncher.common.utils.pasteClipboard
import org.elnix.dragonlauncher.common.utils.semiTransparentIfDisabled
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.enumsui.ColorPickerMode
import org.elnix.dragonlauncher.enumsui.colorPickerText
import org.elnix.dragonlauncher.settings.stores.ColorModesSettingsStore
import org.elnix.dragonlauncher.ui.components.ValidateCancelButtons
import org.elnix.dragonlauncher.ui.components.dragon.DragonIconButton
import org.elnix.dragonlauncher.ui.components.dragon.DragonSurfaceRow
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.dialogs.CustomAlertDialog
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel

@Composable
fun ColorPickerRow(
    label: String,
    showLabel: Boolean = true,
    enabled: Boolean = true,
    currentColor: Color,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onColorPicked: (Color?) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    var actualColor by remember { mutableStateOf(currentColor) }

    val modifier = if (showLabel) Modifier.fillMaxWidth() else Modifier.wrapContentWidth()

    val savedMode by ColorModesSettingsStore.colorPickerMode.asState()
    val initialPage = remember(savedMode) { ColorPickerMode.entries.indexOf(savedMode) }

    DragonSurfaceRow(
        enabled = enabled,
        onClick = { showPicker = true }
    ) {
        if (showLabel) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface.semiTransparentIfDisabled(enabled),
                modifier = Modifier.weight(1f),
                maxLines = Int.MAX_VALUE,
                softWrap = true
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {

            ColorPickerButtonOne(
                currentColor = currentColor,
                onReset = { onColorPicked(null) },
                backgroundColor = backgroundColor,
                onColorPicked = onColorPicked
            )

            ColorPickerButtonTwo(
                currentColor = currentColor,
                onReset = { onColorPicked(null) },
                backgroundColor = backgroundColor,
                onColorPicked = onColorPicked
            )

            Spacer(Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(currentColor, shape = CircleShape)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        CircleShape
                    )
            )
        }
    }

    if (showPicker) {
        CustomAlertDialog(
            imePadding = false,
            modifier = modifier.padding(15.dp),
            onDismissRequest = { showPicker = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = "Reset Color",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .clip(CircleShape)
                        .padding(8.dp)
                        .clickable { onColorPicked(null) }
                )
            },
            title = {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                ColorPicker(
                    color = actualColor,
                    initialPage = initialPage,
                    onColorSelected = { actualColor = it }
                )
            },
            confirmButton = {
                ValidateCancelButtons(
                    onCancel = { showPicker = false }
                ) {
                    onColorPicked(actualColor)
                    showPicker = false
                }
            },
            alignment = Alignment.Center
        )
    }
}


@Composable
private fun ColorPicker(
    color: Color,
    initialPage: Int,
    onColorSelected: (Color) -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val pickerModes = ColorPickerMode.entries
    // Synchronize pager state with stored mode
    val pagerState = rememberPagerState(initialPage = initialPage) { pickerModes.size }

    var hexText by remember { mutableStateOf(color.toHexWithAlpha()) }

    LaunchedEffect(color) {
        hexText = color.toHexWithAlpha()
    }

    // Save the current page as mode whenever changed
    LaunchedEffect(pagerState.currentPage) {
        val currentMode = pickerModes[pagerState.currentPage]
        ColorModesSettingsStore.colorPickerMode.set(ctx, currentMode)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Tabs to indicate and jump
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            pickerModes.forEachIndexed { idx, mode ->
                Text(
                    text = colorPickerText(mode),
                    modifier = Modifier
                        .weight(1f)
                        .clip(DragonShape)
                        .clickable { scope.launch { pagerState.scrollToPage(idx) } }
                        .background(
                            if (pagerState.currentPage == idx) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.surface
                        )
                        .padding(12.dp),
                    color = if (pagerState.currentPage == idx) MaterialTheme.colorScheme.onSecondary
                    else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(5.dp))

        // ───────────── Preview box ─────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color)
                .border(1.dp, MaterialTheme.colorScheme.outline),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val textBoxColor = if (color.luminance() > 0.4) Color.Black else Color.White

                TextField(
                    value = hexText,
                    onValueChange = {
                        if (it.length <= 9) hexText = it
                        runCatching {
                            if (it.startsWith("#") && it.length == 9) {
                                onColorSelected(Color(it.toColorInt()))
                            }
                        }
                    },
                    label = {
                        Text(
                            text = "HEX - AARRGGBB",
                            color = textBoxColor
                        )
                    },
                    colors = AppObjectsColors.outlinedTextFieldColors(
                        backgroundColor = Color.Transparent,
                        onBackgroundColor = textBoxColor,
                        removeBorder = true
                    ),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(50.dp))

                DragonIconButton(
                    onClick = {
                        ctx.copyToClipboard(hexText)
                    },
                    colors = AppObjectsColors.iconButtonColors(color, textBoxColor),
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy HEX"
                )

                DragonIconButton(
                    onClick = {
                        val newColor = pasteColorHexFromClipboard(ctx)
                        newColor?.let { pasted ->
                            hexText = pasted.toHexWithAlpha()
                            onColorSelected(pasted)
                        }
                    },
                    colors = AppObjectsColors.iconButtonColors(color, textBoxColor),
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = "Paste HEX"
                )
            }
        }

        Spacer(Modifier.height(15.dp))

        HorizontalPager(state = pagerState) { page ->
            when (pickerModes[page]) {
                ColorPickerMode.SLIDERS -> SliderColorPicker(
                    actualColor = color,
                    onColorSelected = onColorSelected
                )

                ColorPickerMode.GRADIENT -> GradientColorPicker(
                    initialColor = color,
                    onColorSelected = onColorSelected
                )

                ColorPickerMode.DEFAULTS -> DefaultColorPicker(
                    initialColor = color,
                    onColorSelected = onColorSelected
                )
            }
        }


        Spacer(Modifier.height(12.dp))


        SliderWithLabel(
            label = stringResource(R.string.transparency),
            value = color.alpha,
            color = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.surface.blendWith(
                MaterialTheme.colorScheme.primary,
                0.2f
            ),
            valueRange = 0f..1f
        ) { alpha -> onColorSelected(color.copy(alpha = alpha)) }
    }
}


fun pasteColorHexFromClipboard(ctx: Context): Color? {
    ctx.pasteClipboard()?.let { pasted ->
        try {
            if (pasted.startsWith("#") && pasted.length == 9) {
                return Color(pasted.toColorInt())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ctx.showToast("Error while parsing clipboard color")
            return null
        }
    }
    return null
}
