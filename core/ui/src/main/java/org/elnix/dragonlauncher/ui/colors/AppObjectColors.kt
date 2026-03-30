package org.elnix.dragonlauncher.ui.colors

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.ToggleButtonColors
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.elnix.dragonlauncher.common.utils.alphaMultiplier
import org.elnix.dragonlauncher.common.utils.colors.adjustBrightness
import org.elnix.dragonlauncher.ui.remembers.LocalUseCustomColorChannels

object AppObjectsColors {

    @Composable
    fun switchColors(): SwitchColors {
        val colors = MaterialTheme.colorScheme
        return if (LocalUseCustomColorChannels.current) {
            SwitchDefaults.colors(
                checkedThumbColor = colors.outline,
                checkedTrackColor = colors.primary,
                checkedBorderColor = Color.Transparent,
                uncheckedThumbColor = colors.outline.alphaMultiplier(0.7f),
                uncheckedTrackColor = colors.background,
                uncheckedBorderColor = Color.Transparent,
                disabledCheckedThumbColor = colors.outline.alphaMultiplier(0.5f),
                disabledCheckedTrackColor = colors.primary.alphaMultiplier(0.5f),
                disabledCheckedBorderColor = Color.Transparent,
                disabledUncheckedThumbColor = colors.onSurface.alphaMultiplier(0.5f),
                disabledUncheckedTrackColor = colors.background,
                disabledUncheckedBorderColor = Color.Transparent,
            )
        } else SwitchDefaults.colors()
    }

    @Composable
    fun buttonColors(containerColor: Color? = null): ButtonColors {
        val colors = MaterialTheme.colorScheme
        return if (LocalUseCustomColorChannels.current) {
            ButtonDefaults.buttonColors(
                containerColor = containerColor ?: colors.primary,
                contentColor = colors.onPrimary
            )
        } else ButtonDefaults.buttonColors()
    }

    @Composable
    fun cancelButtonColors(): ButtonColors {
        return if (LocalUseCustomColorChannels.current) {
            ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.error
            )
        } else ButtonDefaults.outlinedButtonColors()
    }


    @Composable
    fun sliderColors(
        activeTrackColor: Color? = null,
        backgroundColor: Color? = null
    ): SliderColors {
        val colors = MaterialTheme.colorScheme
        return if (LocalUseCustomColorChannels.current) {
            SliderDefaults.colors(
                thumbColor = activeTrackColor ?: colors.primary,
                activeTrackColor = activeTrackColor ?: colors.secondary,
                activeTickColor = activeTrackColor ?: colors.primary,
                inactiveTrackColor = backgroundColor ?: colors.surface,
                inactiveTickColor = activeTrackColor ?: colors.primary,
                disabledThumbColor = colors.primary,
                disabledActiveTrackColor = backgroundColor ?: colors.onSurface,
                disabledActiveTickColor = colors.primary,
            )
        } else SliderDefaults.colors()
    }

    @Composable
    fun checkboxColors(): CheckboxColors {
        val colors = MaterialTheme.colorScheme
        return if (LocalUseCustomColorChannels.current) {
            CheckboxDefaults.colors(
                checkedColor = colors.primary,
                uncheckedColor = colors.outline,
                checkmarkColor = colors.onPrimary,
                disabledCheckedColor = colors.primary.alphaMultiplier(0.5f),
                disabledUncheckedColor = colors.outline.alphaMultiplier(0.5f),
                disabledIndeterminateColor = colors.onSurface.alphaMultiplier(0.5f),
            )
        } else CheckboxDefaults.colors()
    }

    @Composable
    fun outlinedTextFieldColors(
        backgroundColor: Color? = null,
        onBackgroundColor: Color? = null,
        removeBorder: Boolean = false
    ): TextFieldColors {
        val colors = MaterialTheme.colorScheme
        return if (LocalUseCustomColorChannels.current) {
            OutlinedTextFieldDefaults.colors(
                focusedTextColor = onBackgroundColor ?: colors.onBackground,
                unfocusedTextColor = onBackgroundColor ?: colors.onBackground,
                disabledTextColor = onBackgroundColor ?: colors.onBackground.adjustBrightness(0.5f),
                errorTextColor = colors.error,

                focusedContainerColor = backgroundColor ?: colors.background,
                unfocusedContainerColor = backgroundColor ?: colors.background,
                disabledContainerColor = backgroundColor ?: colors.background,
                errorContainerColor = backgroundColor ?: colors.background,

                cursorColor = colors.primary,
                errorCursorColor = colors.error,

                focusedBorderColor = if (!removeBorder) colors.primary else Color.Transparent,
                unfocusedBorderColor = if (!removeBorder) colors.outline else Color.Transparent,
                disabledBorderColor = if (!removeBorder) colors.outline.alphaMultiplier(0.5f) else Color.Transparent,
                errorBorderColor = if (!removeBorder) colors.error else Color.Transparent,

                focusedLeadingIconColor = colors.primary,
                unfocusedLeadingIconColor = colors.onSurfaceVariant,
                disabledLeadingIconColor = colors.surfaceVariant,
                errorLeadingIconColor = colors.error,

                focusedTrailingIconColor = colors.primary,
                unfocusedTrailingIconColor = colors.onSurfaceVariant,
                disabledTrailingIconColor = colors.surfaceVariant,
                errorTrailingIconColor = colors.error,

                focusedLabelColor = colors.primary,
                unfocusedLabelColor = colors.outline,
                disabledLabelColor = colors.outline.alphaMultiplier(0.5f),
                errorLabelColor = colors.error,

                focusedPlaceholderColor = colors.outline.alphaMultiplier(0.8f),
                unfocusedPlaceholderColor = colors.outline.alphaMultiplier(0.5f),
                disabledPlaceholderColor = colors.outline.alphaMultiplier(0.3f),
                errorPlaceholderColor = colors.error,

                focusedSupportingTextColor = colors.onSurfaceVariant,
                unfocusedSupportingTextColor = colors.onSurfaceVariant,
                disabledSupportingTextColor = colors.surfaceVariant,
                errorSupportingTextColor = colors.error,

                focusedPrefixColor = colors.onSurfaceVariant,
                unfocusedPrefixColor = colors.onSurfaceVariant,
                disabledPrefixColor = colors.surfaceVariant,
                errorPrefixColor = colors.error,

                focusedSuffixColor = colors.onSurfaceVariant,
                unfocusedSuffixColor = colors.onSurfaceVariant,
                disabledSuffixColor = colors.surfaceVariant,
                errorSuffixColor = colors.error
            )
        } else OutlinedTextFieldDefaults.colors()
    }

    @Composable
    fun radioButtonColors(): RadioButtonColors {
        val colors = MaterialTheme.colorScheme
        return if (LocalUseCustomColorChannels.current) {
            RadioButtonDefaults.colors(
                selectedColor = colors.primary,
                unselectedColor = colors.onSurface,
                disabledSelectedColor = colors.primary.alphaMultiplier(0.5f),
                disabledUnselectedColor = colors.onSurface.alphaMultiplier(0.5f)
            )
        } else RadioButtonDefaults.colors()
    }

    @Composable
    fun iconButtonColors(
        backgroundColor: Color? = null,
        contentColor: Color? = null
    ): IconButtonColors {
        val colors = MaterialTheme.colorScheme
        return if (LocalUseCustomColorChannels.current) {
            IconButtonDefaults.iconButtonColors(
                containerColor = backgroundColor ?: colors.surface,
                contentColor = contentColor ?: colors.primary,
                disabledContainerColor = backgroundColor?.alphaMultiplier(0.5f) ?: colors.surface.alphaMultiplier(0.5f),
                disabledContentColor = contentColor?.alphaMultiplier(0.5f) ?: colors.onSurface.alphaMultiplier(0.5f)
            )
        } else IconButtonDefaults.iconButtonColors()
    }


    @Composable
    fun errorIconButtonColors(): IconButtonColors {
        val colors = MaterialTheme.colorScheme
        return if (LocalUseCustomColorChannels.current) {
            IconButtonDefaults.iconButtonColors(
                containerColor = colors.errorContainer,
                contentColor = colors.error,
                disabledContainerColor = colors.errorContainer.alphaMultiplier(0.5f),
                disabledContentColor = colors.error.alphaMultiplier(0.5f)
            )
        } else IconButtonDefaults.iconButtonColors()
    }

    @Composable
    fun cardColors(): CardColors {
        val colors = MaterialTheme.colorScheme
        return if (LocalUseCustomColorChannels.current) {
            CardDefaults.cardColors(
                colors.surface,
                colors.onSurface,
                colors.surface.alphaMultiplier(0.5f),
                colors.onSurface.alphaMultiplier(0.5f),
            )
        } else CardDefaults.cardColors()
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun toggleButtonColors(): ToggleButtonColors {
        return if (LocalUseCustomColorChannels.current) {
            with(MaterialTheme.colorScheme) {
                ToggleButtonDefaults.toggleButtonColors(
                    containerColor = primary,
                    contentColor = onPrimary,
                    disabledContainerColor = surfaceVariant,
                    disabledContentColor = onSurfaceVariant,
                    checkedContainerColor = surface,
                    checkedContentColor = onSurface
                )
            }
        } else ToggleButtonDefaults.toggleButtonColors()
    }
}
