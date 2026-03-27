package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.JsonArraySettingsStore

object FloatingAppsSettingsStore : JsonArraySettingsStore() {

    override val name: String = "Floating Apps"
    override val dataStoreName: DataStoreName
        get() = DataStoreName.FLOATING_APPS


    override val jsonSetting = Settings.string(
        key = "floating_apps",
        dataStoreName = dataStoreName,
        default = ""
    )

    override val ALL: List<BaseSettingObject<*,*>>
        get() = listOf(this.jsonSetting)
}
