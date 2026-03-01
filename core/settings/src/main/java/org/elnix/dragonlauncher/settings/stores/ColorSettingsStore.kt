package org.elnix.dragonlauncher.settings.stores

import android.content.Context
import androidx.compose.ui.graphics.Color
import org.elnix.dragonlauncher.common.utils.colors.randomColor
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore


object ColorSettingsStore : MapSettingsStore() {
    override val name: String = "Colors"
    override val dataStoreName = DataStoreName.COLOR


    /* ───────────── Colors ───────────── */

    val primaryColor = Settings.color(
        key = "primary_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val onPrimaryColor = Settings.color(
        key = "on_primary_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val secondaryColor = Settings.color(
        key = "secondary_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val onSecondaryColor = Settings.color(
        key = "on_secondary_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val tertiaryColor = Settings.color(
        key = "tertiary_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val onTertiaryColor = Settings.color(
        key = "on_tertiary_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val backgroundColor = Settings.color(
        key = "background_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val onBackgroundColor = Settings.color(
        key = "on_background_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val surfaceColor = Settings.color(
        key = "surface_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val onSurfaceColor = Settings.color(
        key = "on_surface_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val errorColor = Settings.color(
        key = "error_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val onErrorColor = Settings.color(
        key = "on_error_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val outlineColor = Settings.color(
        key = "outline_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val angleLineColor = Settings.color(
        key = "angle_line_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val circleColor = Settings.color(
        key = "circle_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val primaryContainerColor = Settings.color(
        key = "primary_container_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val onPrimaryContainerColor = Settings.color(
        key = "on_primary_container_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val inversePrimaryColor = Settings.color(
        key = "inverse_primary_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val secondaryContainerColor = Settings.color(
        key = "secondary_container_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val onSecondaryContainerColor = Settings.color(
        key = "on_secondary_container_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val tertiaryContainerColor = Settings.color(
        key = "tertiary_container_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val onTertiaryContainerColor = Settings.color(
        key = "on_tertiary_container_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val surfaceVariantColor = Settings.color(
        key = "surface_variant_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val onSurfaceVariantColor = Settings.color(
        key = "on_surface_variant_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val surfaceTintColor = Settings.color(
        key = "surface_tint_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val inverseSurfaceColor = Settings.color(
        key = "inverse_surface_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val inverseOnSurfaceColor = Settings.color(
        key = "inverse_on_surface_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val errorContainerColor = Settings.color(
        key = "error_container_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val onErrorContainerColor = Settings.color(
        key = "on_error_container_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val outlineVariantColor = Settings.color(
        key = "outline_variant_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val scrimColor = Settings.color(
        key = "scrim_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val surfaceBrightColor = Settings.color(
        key = "surface_bright_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val surfaceContainerColor = Settings.color(
        key = "surface_container_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val surfaceContainerHighColor = Settings.color(
        key = "surface_container_high_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val surfaceContainerHighestColor = Settings.color(
        key = "surface_container_highest_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val surfaceContainerLowColor = Settings.color(
        key = "surface_container_low_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val surfaceContainerLowestColor = Settings.color(
        key = "surface_container_lowest_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val surfaceDimColor = Settings.color(
        key = "surface_dim_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val primaryFixedColor = Settings.color(
        key = "primary_fixed_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val primaryFixedDimColor = Settings.color(
        key = "primary_fixed_dim_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val onPrimaryFixedColor = Settings.color(
        key = "on_primary_fixed_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val onPrimaryFixedVariantColor = Settings.color(
        key = "on_primary_fixed_variant_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val secondaryFixedColor = Settings.color(
        key = "secondary_fixed_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val secondaryFixedDimColor = Settings.color(
        key = "secondary_fixed_dim_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val onSecondaryFixedColor = Settings.color(
        key = "on_secondary_fixed_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val onSecondaryFixedVariantColor = Settings.color(
        key = "on_secondary_fixed_variant_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val tertiaryFixedColor = Settings.color(
        key = "tertiary_fixed_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val tertiaryFixedDimColor = Settings.color(
        key = "tertiary_fixed_dim_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val onTertiaryFixedColor = Settings.color(
        key = "on_tertiary_fixed_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val onTertiaryFixedVariantColor = Settings.color(
        key = "on_tertiary_fixed_variant_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )


    /* ───────────── Action colors ───────────── */

    val launchAppColor = Settings.color(
        key = "launch_app_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val openUrlColor = Settings.color(
        key = "open_url_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val notificationShadeColor = Settings.color(
        key = "notification_shade_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val controlPanelColor = Settings.color(
        key = "control_panel_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val openAppDrawerColor = Settings.color(
        key = "open_app_drawer_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val launcherSettingsColor = Settings.color(
        key = "launcher_settings_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val lockColor = Settings.color(
        key = "lock_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val openFileColor = Settings.color(
        key = "open_file_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val reloadColor = Settings.color(
        key = "reload_color",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val openRecentAppsColor = Settings.color(
        key = "open_recent_apps",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val openCircleNestColor = Settings.color(
        key = "open_circle_nest",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val goParentNestColor = Settings.color(
        key = "go_parent_nest",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    /* ───────────── Registry ───────────── */

    override val ALL: List<BaseSettingObject<Color, Int>>
        get() = listOf(
            primaryColor,
            onPrimaryColor,
            primaryContainerColor,
            onPrimaryContainerColor,
            inversePrimaryColor,
            secondaryColor,
            onSecondaryColor,
            secondaryContainerColor,
            onSecondaryContainerColor,
            tertiaryColor,
            onTertiaryColor,
            tertiaryContainerColor,
            onTertiaryContainerColor,
            backgroundColor,
            onBackgroundColor,
            surfaceColor,
            onSurfaceColor,
            surfaceVariantColor,
            onSurfaceVariantColor,
            surfaceTintColor,
            inverseSurfaceColor,
            inverseOnSurfaceColor,
            errorColor,
            onErrorColor,
            errorContainerColor,
            onErrorContainerColor,
            outlineColor,
            outlineVariantColor,
            scrimColor,
            surfaceBrightColor,
            surfaceContainerColor,
            surfaceContainerHighColor,
            surfaceContainerHighestColor,
            surfaceContainerLowColor,
            surfaceContainerLowestColor,
            surfaceDimColor,
            primaryFixedColor,
            primaryFixedDimColor,
            onPrimaryFixedColor,
            onPrimaryFixedVariantColor,
            secondaryFixedColor,
            secondaryFixedDimColor,
            onSecondaryFixedColor,
            onSecondaryFixedVariantColor,
            tertiaryFixedColor,
            tertiaryFixedDimColor,
            onTertiaryFixedColor,
            onTertiaryFixedVariantColor,
            angleLineColor,
            circleColor,
            launchAppColor,
            openUrlColor,
            notificationShadeColor,
            controlPanelColor,
            openAppDrawerColor,
            launcherSettingsColor,
            lockColor,
            openFileColor,
            reloadColor,
            openRecentAppsColor,
            openCircleNestColor,
            goParentNestColor
        )

    suspend fun setAllRandomColors(ctx: Context) {
        setAllColors(ctx) { randomColor() }
    }

    suspend fun setAllSameColors(ctx: Context, color: Color) {
        setAllColors(ctx) { color }
    }

    suspend fun setAllColors(ctx: Context, color: () -> Color) {
        ALL.forEach { it.set(ctx, color()) }
    }

    // For test mode backup
    private val backupColorsMap = mutableMapOf<String, Color>()

    suspend fun backupColors(ctx: Context) {
        backupColorsMap.clear()
        ALL.forEach { setting ->
            backupColorsMap[setting.key] = setting.get(ctx)
        }
    }

    suspend fun restoreColors(ctx: Context) {
        backupColorsMap.forEach { (key, color) ->
            ALL.find { it.key == key }?.set(ctx, color)
        }
        backupColorsMap.clear()
    }
}
