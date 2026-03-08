package org.elnix.dragonlauncher.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.elnix.dragonlauncher.settings.DataStoreName.ANGLE_LINE
import org.elnix.dragonlauncher.settings.DataStoreName.BACKUP
import org.elnix.dragonlauncher.settings.DataStoreName.BEHAVIOR
import org.elnix.dragonlauncher.settings.DataStoreName.COLOR
import org.elnix.dragonlauncher.settings.DataStoreName.COLOR_MODE
import org.elnix.dragonlauncher.settings.DataStoreName.DEBUG
import org.elnix.dragonlauncher.settings.DataStoreName.DRAWER
import org.elnix.dragonlauncher.settings.DataStoreName.FLOATING_APPS
import org.elnix.dragonlauncher.settings.DataStoreName.LANGUAGE
import org.elnix.dragonlauncher.settings.DataStoreName.PRIVATE_APPS
import org.elnix.dragonlauncher.settings.DataStoreName.PRIVATE_SETTINGS
import org.elnix.dragonlauncher.settings.DataStoreName.STATUS_BAR
import org.elnix.dragonlauncher.settings.DataStoreName.STATUS_BAR_JSON
import org.elnix.dragonlauncher.settings.DataStoreName.SWIPE
import org.elnix.dragonlauncher.settings.DataStoreName.SWIPE_MAP
import org.elnix.dragonlauncher.settings.DataStoreName.UI
import org.elnix.dragonlauncher.settings.DataStoreName.WELLBEING
import org.elnix.dragonlauncher.settings.DataStoreName.WORKSPACES
import org.elnix.dragonlauncher.settings.bases.BaseSettingsStore
import org.elnix.dragonlauncher.settings.stores.AngleLineSettingsStore
import org.elnix.dragonlauncher.settings.stores.BackupSettingsStore
import org.elnix.dragonlauncher.settings.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.settings.stores.ColorModesSettingsStore
import org.elnix.dragonlauncher.settings.stores.ColorSettingsStore
import org.elnix.dragonlauncher.settings.stores.DebugSettingsStore
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.settings.stores.FloatingAppsSettingsStore
import org.elnix.dragonlauncher.settings.stores.LanguageSettingsStore
import org.elnix.dragonlauncher.settings.stores.PrivateAppsSettingsStore
import org.elnix.dragonlauncher.settings.stores.PrivateSettingsStore
import org.elnix.dragonlauncher.settings.stores.StatusBarJsonSettingsStore
import org.elnix.dragonlauncher.settings.stores.StatusBarSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeMapSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.settings.stores.WellbeingSettingsStore
import org.elnix.dragonlauncher.settings.stores.WorkspaceSettingsStore

enum class DataStoreName(
    val value: String,
    val backupKey: String,
    val userBackup: Boolean = true
) {
    UI("uiDatastore", "ui"),
    COLOR_MODE("colorModeDatastore", "color_mode"),
    COLOR("colorDatastore", "color"),
    PRIVATE_SETTINGS("privateSettingsStore", "private", false),
    SWIPE("swipePointsDatastore", "new_actions"),
    LANGUAGE("languageDatastore", "language"),
    DRAWER("drawerDatastore", "drawer"),
    DEBUG("debugDatastore", "debug"),
    WORKSPACES("workspacesDataStore", "workspaces"),
    PRIVATE_APPS("privateAppsDatastore","private_apps", false),
    BEHAVIOR("behaviorDatastore", "behavior"),
    BACKUP("backupDatastore", "backup"),
    STATUS_BAR("statusDatastore", "status_bar"),
    FLOATING_APPS("floatingAppsDatastore", "floating_apps"),
    WELLBEING("wellbeingDatastore", "wellbeing"),
    SWIPE_MAP("swipeMapDataStore", "swipe_map"),
    STATUS_BAR_JSON("statusBarJsonDataStore", "status_bar_json"),
    ANGLE_LINE("AngleLineDatastore", "angle_line")
}


object SettingsStoreRegistry {
    val byName: Map<DataStoreName, BaseSettingsStore<*,*>> = mapOf(
        UI to UiSettingsStore,
        COLOR_MODE to ColorModesSettingsStore,
        COLOR to ColorSettingsStore,
        PRIVATE_SETTINGS to PrivateSettingsStore,
        SWIPE to SwipeSettingsStore,
        LANGUAGE to LanguageSettingsStore,
        DRAWER to DrawerSettingsStore,
        DEBUG to DebugSettingsStore,
        WORKSPACES to WorkspaceSettingsStore,
        PRIVATE_APPS to PrivateAppsSettingsStore,
        BEHAVIOR to BehaviorSettingsStore,
        BACKUP to BackupSettingsStore,
        STATUS_BAR to StatusBarSettingsStore,
        FLOATING_APPS to FloatingAppsSettingsStore,
        WELLBEING to WellbeingSettingsStore,
        SWIPE_MAP to SwipeMapSettingsStore,
        STATUS_BAR_JSON to StatusBarJsonSettingsStore,
        ANGLE_LINE to AngleLineSettingsStore
    )
}

val allStores = SettingsStoreRegistry.byName


val backupableStores =
    SettingsStoreRegistry.byName
        .filterKeys { it.userBackup }


/**
 * Datastore, now handled by a conditional function to avoid errors, all private
 */
private val Context.uiDatastore by preferencesDataStore(name = UI.value)
private val Context.colorModeDatastore by preferencesDataStore(name = COLOR_MODE.value)
private val Context.colorDatastore by preferencesDataStore(name = COLOR.value)
private val Context.privateSettingsStore by preferencesDataStore(name = PRIVATE_SETTINGS.value)
private val Context.swipeDataStore by preferencesDataStore(name = SWIPE.value)
private val Context.languageDatastore by preferencesDataStore(name = LANGUAGE.value)
private val Context.drawerDataStore by preferencesDataStore(name = DRAWER.value)
private val Context.debugDatastore by preferencesDataStore(name = DEBUG.value)
private val Context.workspaceDataStore by preferencesDataStore(name = WORKSPACES.value)
private val Context.privateAppsDatastore by preferencesDataStore(name = PRIVATE_APPS.value)
private val Context.behaviorDataStore by preferencesDataStore(name = BEHAVIOR.value)
private val Context.backupDatastore by preferencesDataStore(name = BACKUP.value)
private val Context.statusBarDatastore by preferencesDataStore(name = STATUS_BAR.value)
private val Context.floatingAppsDatastore by preferencesDataStore(name = FLOATING_APPS.value)
private val Context.wellbeingDatastore by preferencesDataStore(name = WELLBEING.value)
private val Context.swipeMapDatastore by preferencesDataStore(name = SWIPE_MAP.value)
private val Context.statusBarJsonDataStore by preferencesDataStore(name = STATUS_BAR_JSON.value)
private val Context.angleLineDatastore by preferencesDataStore(name = ANGLE_LINE.value)



fun Context.resolveDataStore(name: DataStoreName): DataStore<Preferences> {
    val appCtx = applicationContext
    return when (name) {
        DataStoreName.UI -> appCtx.uiDatastore
        DataStoreName.COLOR_MODE -> appCtx.colorModeDatastore
        DataStoreName.COLOR -> appCtx.colorDatastore
        DataStoreName.PRIVATE_SETTINGS -> appCtx.privateSettingsStore
        DataStoreName.SWIPE -> appCtx.swipeDataStore
        DataStoreName.LANGUAGE -> appCtx.languageDatastore
        DataStoreName.DRAWER -> appCtx.drawerDataStore
        DataStoreName.DEBUG -> appCtx.debugDatastore
        DataStoreName.WORKSPACES -> appCtx.workspaceDataStore
        DataStoreName.PRIVATE_APPS -> appCtx.privateAppsDatastore
        DataStoreName.BEHAVIOR -> appCtx.behaviorDataStore
        DataStoreName.BACKUP -> appCtx.backupDatastore
        DataStoreName.STATUS_BAR -> appCtx.statusBarDatastore
        DataStoreName.FLOATING_APPS -> appCtx.floatingAppsDatastore
        DataStoreName.WELLBEING -> appCtx.wellbeingDatastore
        DataStoreName.SWIPE_MAP -> appCtx.swipeMapDatastore
        DataStoreName.STATUS_BAR_JSON -> appCtx.statusBarJsonDataStore
        DataStoreName.ANGLE_LINE -> appCtx.angleLineDatastore
    }
}
