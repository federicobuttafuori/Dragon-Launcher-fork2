package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.enumsui.ColorPickerButtonAction
import org.elnix.dragonlauncher.enumsui.ColorPickerMode
import org.elnix.dragonlauncher.enumsui.DefaultThemes
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object ColorModesSettingsStore : MapSettingsStore() {

    override val name: String = "Color Modes"
    override val dataStoreName = DataStoreName.COLOR_MODE


    override val ALL: List<BaseSettingObject <*, *> >
        get() = listOf(
            colorPickerMode,
            defaultTheme,
            colorPickerButtonOne,
            colorPickerButtonTwo,
            dynamicColor,
            colorTestMode
        )


    val colorPickerMode = Settings.enum(
        key = "colorPickerMode",
        dataStoreName = dataStoreName,
        default = ColorPickerMode.DEFAULTS,
        enumClass = ColorPickerMode::class.java,
    )

    val defaultTheme = Settings.enum(
        key = "defaultTheme",
        dataStoreName = dataStoreName,
        default = DefaultThemes.AMOLED,
        enumClass = DefaultThemes::class.java
    )

    val colorPickerButtonOne = Settings.enum(
        key = "colorPickerButton",
        dataStoreName = dataStoreName,
        default = ColorPickerButtonAction.RANDOM,
        enumClass = ColorPickerButtonAction::class.java
    )
    val colorPickerButtonTwo = Settings.enum(
        key = "colorPickerButtonTwo",
        dataStoreName = dataStoreName,
        default = ColorPickerButtonAction.COPY,
        enumClass = ColorPickerButtonAction::class.java
    )

    val dynamicColor = Settings.boolean(
        key = "dynamicColor",
        dataStoreName = dataStoreName,
        default = false
    )

    val colorTestMode = Settings.boolean(
        key = "colorTestMode",
        dataStoreName = dataStoreName,
        default = false
    )
}
