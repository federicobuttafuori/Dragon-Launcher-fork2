package org.elnix.dragonlauncher.settings.stores

import androidx.compose.ui.graphics.Color
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object StatusBarSettingsStore : MapSettingsStore() {

    override val name: String = "Status Bar"
    override val dataStoreName: DataStoreName = DataStoreName.STATUS_BAR

    val showStatusBar = Settings.boolean(
        key = "showStatusBar",
        dataStoreName = dataStoreName,
        default = false
    )

    val barBackgroundColor = Settings.color(
        key = "barBackgroundColor",
        dataStoreName = dataStoreName,
        default = Color.Transparent
    )

    val barTextColor = Settings.color(
        key = "barTextColor",
        dataStoreName = dataStoreName,
        default = Color.White
    )

    val leftPadding = Settings.int(
        key = "leftPadding",
        dataStoreName = dataStoreName,
        default = 5,
        allowedRange = 0..300
    )

    val rightPadding = Settings.int(
        key = "rightPadding",
        dataStoreName = dataStoreName,
        default = 5,
        allowedRange = 0..300
    )

    val topPadding = Settings.int(
        key = "topPadding",
        dataStoreName = dataStoreName,
        default = 2,
        allowedRange = 0..300
    )

    val bottomPadding = Settings.int(
        key = "bottomPadding",
        dataStoreName = dataStoreName,
        default = 2,
        allowedRange = 0..300
    )


    override val ALL: List<BaseSettingObject<*,*>> = listOf(
        showStatusBar,
        barBackgroundColor,
        barTextColor,
        leftPadding,
        rightPadding,
        topPadding,
        bottomPadding
    )
}
