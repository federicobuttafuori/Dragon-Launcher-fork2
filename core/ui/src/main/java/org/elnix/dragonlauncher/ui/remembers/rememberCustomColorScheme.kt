package org.elnix.dragonlauncher.ui.remembers

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.utils.Constants.Logging.COLORS_TAG
import org.elnix.dragonlauncher.common.utils.colors.toHexWithAlpha
import org.elnix.dragonlauncher.common.utils.definedOrNull
import org.elnix.dragonlauncher.settings.stores.ColorSettingsStore
import org.elnix.dragonlauncher.ui.components.settings.asStateNull

@Composable
internal fun rememberCustomColorScheme(defaultColorScheme: ColorScheme): ColorScheme {

    val ctx = LocalContext.current


    /* ───────────── PRIMARY ───────────── */
    val primary by ColorSettingsStore.primaryColor.asStateNull()
    val onPrimary by ColorSettingsStore.onPrimaryColor.asStateNull()
    val primaryContainer by ColorSettingsStore.primaryContainerColor.asStateNull()
    val onPrimaryContainer by ColorSettingsStore.onPrimaryContainerColor.asStateNull()
    val inversePrimary by ColorSettingsStore.inversePrimaryColor.asStateNull()
    val primaryFixed by ColorSettingsStore.primaryFixedColor.asStateNull()
    val primaryFixedDim by ColorSettingsStore.primaryFixedDimColor.asStateNull()
    val onPrimaryFixed by ColorSettingsStore.onPrimaryFixedColor.asStateNull()
    val onPrimaryFixedVariant by ColorSettingsStore.onPrimaryFixedVariantColor.asStateNull()


    /* ───────────── SECONDARY ───────────── */
    val secondary by ColorSettingsStore.secondaryColor.asStateNull()
    val onSecondary by ColorSettingsStore.onSecondaryColor.asStateNull()
    val secondaryContainer by ColorSettingsStore.secondaryContainerColor.asStateNull()
    val onSecondaryContainer by ColorSettingsStore.onSecondaryContainerColor.asStateNull()
    val secondaryFixed by ColorSettingsStore.secondaryFixedColor.asStateNull()
    val secondaryFixedDim by ColorSettingsStore.secondaryFixedDimColor.asStateNull()
    val onSecondaryFixed by ColorSettingsStore.onSecondaryFixedColor.asStateNull()
    val onSecondaryFixedVariant by ColorSettingsStore.onSecondaryFixedVariantColor.asStateNull()


    /* ───────────── TERTIARY ───────────── */
    val tertiary by ColorSettingsStore.tertiaryColor.asStateNull()
    val onTertiary by ColorSettingsStore.onTertiaryColor.asStateNull()
    val tertiaryContainer by ColorSettingsStore.tertiaryContainerColor.asStateNull()
    val onTertiaryContainer by ColorSettingsStore.onTertiaryContainerColor.asStateNull()
    val tertiaryFixed by ColorSettingsStore.tertiaryFixedColor.asStateNull()
    val tertiaryFixedDim by ColorSettingsStore.tertiaryFixedDimColor.asStateNull()
    val onTertiaryFixed by ColorSettingsStore.onTertiaryFixedColor.asStateNull()
    val onTertiaryFixedVariant by ColorSettingsStore.onTertiaryFixedVariantColor.asStateNull()


    /* ───────────── BACKGROUND / SURFACE ───────────── */
    val background by ColorSettingsStore.backgroundColor.asStateNull()
    val onBackground by ColorSettingsStore.onBackgroundColor.asStateNull()
    val surface by ColorSettingsStore.surfaceColor.asStateNull()
    val onSurface by ColorSettingsStore.onSurfaceColor.asStateNull()
    val surfaceVariant by ColorSettingsStore.surfaceVariantColor.asStateNull()
    val onSurfaceVariant by ColorSettingsStore.onSurfaceVariantColor.asStateNull()
    val surfaceTint by ColorSettingsStore.surfaceTintColor.asStateNull()
    val inverseSurface by ColorSettingsStore.inverseSurfaceColor.asStateNull()
    val inverseOnSurface by ColorSettingsStore.inverseOnSurfaceColor.asStateNull()


    /* ───────────── SURFACE CONTAINERS ───────────── */
    val surfaceBright by ColorSettingsStore.surfaceBrightColor.asStateNull()
    val surfaceDim by ColorSettingsStore.surfaceDimColor.asStateNull()
    val surfaceContainer by ColorSettingsStore.surfaceContainerColor.asStateNull()
    val surfaceContainerLow by ColorSettingsStore.surfaceContainerLowColor.asStateNull()
    val surfaceContainerLowest by ColorSettingsStore.surfaceContainerLowestColor.asStateNull()
    val surfaceContainerHigh by ColorSettingsStore.surfaceContainerHighColor.asStateNull()
    val surfaceContainerHighest by ColorSettingsStore.surfaceContainerHighestColor.asStateNull()


    /* ───────────── ERROR ───────────── */
    val error by ColorSettingsStore.errorColor.asStateNull()
    val onError by ColorSettingsStore.onErrorColor.asStateNull()
    val errorContainer by ColorSettingsStore.errorContainerColor.asStateNull()
    val onErrorContainer by ColorSettingsStore.onErrorContainerColor.asStateNull()


    /* ───────────── OUTLINE / MISC ───────────── */
    val outline by ColorSettingsStore.outlineColor.asStateNull()
    val outlineVariant by ColorSettingsStore.outlineVariantColor.asStateNull()
    val scrim by ColorSettingsStore.scrimColor.asStateNull()



    ctx.logD(COLORS_TAG) {
        "Primary: ${primary.toHexWithAlpha()}, definedOrNull: ${
            primary.definedOrNull().toHexWithAlpha()
        }, default fallback: ${defaultColorScheme.primary.toHexWithAlpha()}"
    }
    ctx.logD(COLORS_TAG) { "Used: ${(primary.definedOrNull() ?: defaultColorScheme.primary).toHexWithAlpha()}" }

    return ColorScheme(
        primary = primary.definedOrNull() ?: defaultColorScheme.primary,
        onPrimary = onPrimary.definedOrNull() ?: defaultColorScheme.onPrimary,
        primaryContainer = primaryContainer.definedOrNull() ?: defaultColorScheme.primaryContainer,
        onPrimaryContainer = onPrimaryContainer.definedOrNull() ?: defaultColorScheme.onPrimaryContainer,
        inversePrimary = inversePrimary.definedOrNull() ?: defaultColorScheme.inversePrimary,

        secondary = secondary.definedOrNull() ?: defaultColorScheme.secondary,
        onSecondary = onSecondary.definedOrNull() ?: defaultColorScheme.onSecondary,
        secondaryContainer = secondaryContainer.definedOrNull() ?: defaultColorScheme.secondaryContainer,
        onSecondaryContainer = onSecondaryContainer.definedOrNull() ?: defaultColorScheme.onSecondaryContainer,

        tertiary = tertiary.definedOrNull() ?: defaultColorScheme.tertiary,
        onTertiary = onTertiary.definedOrNull() ?: defaultColorScheme.onTertiary,
        tertiaryContainer = tertiaryContainer.definedOrNull() ?: defaultColorScheme.tertiaryContainer,
        onTertiaryContainer = onTertiaryContainer.definedOrNull() ?: defaultColorScheme.onTertiaryContainer,

        background = background.definedOrNull() ?: defaultColorScheme.background,
        onBackground = onBackground.definedOrNull() ?: defaultColorScheme.onBackground,

        surface = surface.definedOrNull() ?: defaultColorScheme.surface,
        onSurface = onSurface.definedOrNull() ?: defaultColorScheme.onSurface,
        surfaceVariant = surfaceVariant.definedOrNull() ?: defaultColorScheme.surfaceVariant,
        onSurfaceVariant = onSurfaceVariant.definedOrNull() ?: defaultColorScheme.onSurfaceVariant,
        surfaceTint = surfaceTint.definedOrNull() ?: defaultColorScheme.surfaceTint,

        inverseSurface = inverseSurface.definedOrNull() ?: defaultColorScheme.inverseSurface,
        inverseOnSurface = inverseOnSurface.definedOrNull() ?: defaultColorScheme.inverseOnSurface,

        error = error.definedOrNull() ?: defaultColorScheme.error,
        onError = onError.definedOrNull() ?: defaultColorScheme.onError,
        errorContainer = errorContainer.definedOrNull() ?: defaultColorScheme.errorContainer,
        onErrorContainer = onErrorContainer.definedOrNull() ?: defaultColorScheme.onErrorContainer,

        outline = outline.definedOrNull() ?: defaultColorScheme.outline,
        outlineVariant = outlineVariant.definedOrNull() ?: defaultColorScheme.outlineVariant,
        scrim = scrim.definedOrNull() ?: defaultColorScheme.scrim,

        surfaceBright = surfaceBright.definedOrNull() ?: defaultColorScheme.surfaceBright,
        surfaceContainer = surfaceContainer.definedOrNull() ?: defaultColorScheme.surfaceContainer,
        surfaceContainerHigh = surfaceContainerHigh.definedOrNull() ?: defaultColorScheme.surfaceContainerHigh,
        surfaceContainerHighest = surfaceContainerHighest.definedOrNull() ?: defaultColorScheme.surfaceContainerHighest,
        surfaceContainerLow = surfaceContainerLow.definedOrNull() ?: defaultColorScheme.surfaceContainerLow,
        surfaceContainerLowest = surfaceContainerLowest.definedOrNull() ?: defaultColorScheme.surfaceContainerLowest,
        surfaceDim = surfaceDim.definedOrNull() ?: defaultColorScheme.surfaceDim,

        primaryFixed = primaryFixed.definedOrNull() ?: defaultColorScheme.primaryFixed,
        primaryFixedDim = primaryFixedDim.definedOrNull() ?: defaultColorScheme.primaryFixedDim,
        onPrimaryFixed = onPrimaryFixed.definedOrNull() ?: defaultColorScheme.onPrimaryFixed,
        onPrimaryFixedVariant = onPrimaryFixedVariant.definedOrNull() ?: defaultColorScheme.onPrimaryFixedVariant,

        secondaryFixed = secondaryFixed.definedOrNull() ?: defaultColorScheme.secondaryFixed,
        secondaryFixedDim = secondaryFixedDim.definedOrNull() ?: defaultColorScheme.secondaryFixedDim,
        onSecondaryFixed = onSecondaryFixed.definedOrNull() ?: defaultColorScheme.onSecondaryFixed,
        onSecondaryFixedVariant = onSecondaryFixedVariant.definedOrNull() ?: defaultColorScheme.onSecondaryFixedVariant,

        tertiaryFixed = tertiaryFixed.definedOrNull() ?: defaultColorScheme.tertiaryFixed,
        tertiaryFixedDim = tertiaryFixedDim.definedOrNull() ?: defaultColorScheme.tertiaryFixedDim,
        onTertiaryFixed = onTertiaryFixed.definedOrNull() ?: defaultColorScheme.onTertiaryFixed,
        onTertiaryFixedVariant = onTertiaryFixedVariant.definedOrNull() ?: defaultColorScheme.onTertiaryFixedVariant,
    )
}
