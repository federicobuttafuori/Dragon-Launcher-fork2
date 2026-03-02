package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object DebugSettingsStore : MapSettingsStore() {
    override val name: String = "Debug"
    override val dataStoreName = DataStoreName.DEBUG

    val debugEnabled = Settings.boolean(
        key = "debugEnabled",
        dataStoreName = dataStoreName,
        default = false
    )

    val debugInfos = Settings.boolean(
        key = "debugInfos",
        dataStoreName = dataStoreName,
        default = false
    )

    val settingsDebugInfo = Settings.boolean(
        key = "settingsDebugInfo",
        dataStoreName = dataStoreName,
        default = false
    )

    val widgetsDebugInfo = Settings.boolean(
        key = "widgetsDebugInfo",
        dataStoreName = dataStoreName,
        default = false
    )

    val workspacesDebugInfo = Settings.boolean(
        key = "workspacesDebugInfo",
        dataStoreName = dataStoreName,
        default = false
    )

    val forceAppLanguageSelector = Settings.boolean(
        key = "forceAppLanguageSelector",
        dataStoreName = dataStoreName,
        default = false
    )

    val forceAppWidgetsSelector = Settings.boolean(
        key = "forceAppWidgetsSelector",
        dataStoreName = dataStoreName,
        default = false
    )
//    val forceAppWidgetsBinding = Settings.boolean(
//        key = "forceAppWidgetsBinding",
//        dataStoreName = dataStoreName,
//        default = false
//    )
    val autoRaiseDragonOnSystemLauncher = Settings.boolean(
        key = "autoRaiseDragonOnSystemLauncher",
        dataStoreName = dataStoreName,
        default = false
    )

    val systemLauncherPackageName = Settings.string(
        key = "systemLauncherPackageName",
        dataStoreName = dataStoreName,
        default = ""
    )

    val useAccessibilityInsteadOfContextToExpandActionPanel = Settings.boolean(
        key = "useAccessibilityInsteadOfContextToExpandActionPanel",
        dataStoreName = dataStoreName,
        default = true
    )

    val enableLogging = Settings.boolean(
        key = "enableLogging",
        dataStoreName = dataStoreName,
        default = false
    )

    val privateSpaceDebugInfo = Settings.boolean(
        key = "privateSpaceDebugInfo",
        dataStoreName = dataStoreName,
        default = false
    )


    override val ALL: List<BaseSettingObject<*,*>>
        get() = listOf(
            debugEnabled,
            debugInfos,
            settingsDebugInfo,
            widgetsDebugInfo,
            workspacesDebugInfo,
            forceAppLanguageSelector,
            forceAppWidgetsSelector,
//            forceAppWidgetsBinding,
            autoRaiseDragonOnSystemLauncher,
            systemLauncherPackageName,
            useAccessibilityInsteadOfContextToExpandActionPanel,
            enableLogging,
            privateSpaceDebugInfo
        )
    }
