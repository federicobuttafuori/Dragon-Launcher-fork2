package org.elnix.dragonlauncher.ui.remembers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import org.elnix.dragonlauncher.settings.stores.ColorSettingsStore
import org.elnix.dragonlauncher.ui.base.asStateNull
import org.elnix.dragonlauncher.base.theme.DefaultExtraColors
import org.elnix.dragonlauncher.base.theme.ExtraColors
import org.elnix.dragonlauncher.common.utils.colors.ColorUtils.definedOrNull

@Composable
fun rememberExtraColors(): ExtraColors {

    val angleLineColor by ColorSettingsStore.angleLineColor.asStateNull()
    val circleColor by ColorSettingsStore.circleColor.asStateNull()

    val launchAppColor by ColorSettingsStore.launchAppColor.asStateNull()
    val openUrlColor by ColorSettingsStore.openUrlColor.asStateNull()
    val notificationShadeColor by ColorSettingsStore.notificationShadeColor.asStateNull()
    val controlPanelColor by ColorSettingsStore.controlPanelColor.asStateNull()
    val openAppDrawerColor by ColorSettingsStore.openAppDrawerColor.asStateNull()
    val launcherSettingsColor by ColorSettingsStore.launcherSettingsColor.asStateNull()
    val lockColor by ColorSettingsStore.lockColor.asStateNull()
    val openFileColor by ColorSettingsStore.openFileColor.asStateNull()
    val reloadAppsColor by ColorSettingsStore.reloadColor.asStateNull()
    val openRecentAppsColor by ColorSettingsStore.openRecentAppsColor.asStateNull()
    val openCircleNestColor by ColorSettingsStore.openCircleNestColor.asStateNull()
    val goParentNestColor by ColorSettingsStore.goParentNestColor.asStateNull()
    val toggleBluetooth by ColorSettingsStore.toggleBluetooth.asStateNull()
    val toggleData by ColorSettingsStore.toggleData.asStateNull()
    val toggleWifi by ColorSettingsStore.toggleWifi.asStateNull()
    val runAdbCommand by ColorSettingsStore.runAdbCommand.asStateNull()

    return ExtraColors(
        angleLine = angleLineColor.definedOrNull() ?: DefaultExtraColors.angleLine,
        circle = circleColor.definedOrNull() ?: DefaultExtraColors.circle,

        launchApp = launchAppColor.definedOrNull() ?: DefaultExtraColors.launchApp,
        openUrl = openUrlColor.definedOrNull() ?: DefaultExtraColors.openUrl,
        notificationShade = notificationShadeColor.definedOrNull() ?: DefaultExtraColors.notificationShade,
        controlPanel = controlPanelColor.definedOrNull() ?: DefaultExtraColors.controlPanel,
        openAppDrawer = openAppDrawerColor.definedOrNull() ?: DefaultExtraColors.openAppDrawer,
        launcherSettings = launcherSettingsColor.definedOrNull() ?: DefaultExtraColors.launcherSettings,
        lock = lockColor.definedOrNull() ?: DefaultExtraColors.lock,
        openFile = openFileColor.definedOrNull() ?: DefaultExtraColors.openFile,
        reload = reloadAppsColor.definedOrNull() ?: DefaultExtraColors.reload,
        openRecentApps = openRecentAppsColor.definedOrNull() ?: DefaultExtraColors.openRecentApps,
        openCircleNest = openCircleNestColor.definedOrNull() ?: DefaultExtraColors.openCircleNest,
        goParentNest = goParentNestColor.definedOrNull() ?: DefaultExtraColors.goParentNest,
        toggleBluetooth = toggleBluetooth.definedOrNull() ?: DefaultExtraColors.toggleBluetooth,
        toggleData = toggleData.definedOrNull() ?: DefaultExtraColors.toggleData,
        toggleWifi = toggleWifi.definedOrNull() ?: DefaultExtraColors.toggleWifi,
        runAdbCommand = runAdbCommand.definedOrNull() ?: DefaultExtraColors.runAdbCommand,
    )
}
