package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object SwipeMapSettingsStore : MapSettingsStore() {

    override val name: String = "Swipe Map"
    override val dataStoreName = DataStoreName.SWIPE_MAP

    override val ALL: List<BaseSettingObject <*, *> >
        get() = listOf(
            subNestDefaultRadius
        )


    val subNestDefaultRadius = Settings.int(
        key = "subNestDefaultRadius",
        dataStoreName = dataStoreName,
        default = 35,
        allowedRange = 0..50
    )
}
