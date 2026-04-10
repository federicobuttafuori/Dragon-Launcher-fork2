package org.elnix.dragonlauncher.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import org.elnix.dragonlauncher.settings.DataStoreName.ANGLE_LINE
import org.elnix.dragonlauncher.settings.DataStoreName.BACKUP
import org.elnix.dragonlauncher.settings.DataStoreName.BEHAVIOR
import org.elnix.dragonlauncher.settings.DataStoreName.COLOR
import org.elnix.dragonlauncher.settings.DataStoreName.COLOR_MODE
import org.elnix.dragonlauncher.settings.DataStoreName.DEBUG
import org.elnix.dragonlauncher.settings.DataStoreName.DRAWER
import org.elnix.dragonlauncher.settings.DataStoreName.HOLD_TO_ACTIVATE
import org.elnix.dragonlauncher.settings.DataStoreName.LANGUAGE
import org.elnix.dragonlauncher.settings.DataStoreName.LEGACY_FLOATING_APPS
import org.elnix.dragonlauncher.settings.DataStoreName.PRIVATE_APPS
import org.elnix.dragonlauncher.settings.DataStoreName.PRIVATE_SETTINGS
import org.elnix.dragonlauncher.settings.DataStoreName.STATUS_BAR
import org.elnix.dragonlauncher.settings.DataStoreName.STATUS_BAR_JSON
import org.elnix.dragonlauncher.settings.DataStoreName.SWIPE
import org.elnix.dragonlauncher.settings.DataStoreName.SWIPE_MAP
import org.elnix.dragonlauncher.settings.DataStoreName.UI
import org.elnix.dragonlauncher.settings.DataStoreName.WELLBEING
import org.elnix.dragonlauncher.settings.DataStoreName.WIDGETS
import org.elnix.dragonlauncher.settings.DataStoreName.WORKSPACES
import org.elnix.dragonlauncher.settings.bases.BaseSettingsStore
import org.elnix.dragonlauncher.settings.bases.DatastoreProvider
import org.elnix.dragonlauncher.settings.stores.AngleLineSettingsStore
import org.elnix.dragonlauncher.settings.stores.BackupSettingsStore
import org.elnix.dragonlauncher.settings.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.settings.stores.ColorModesSettingsStore
import org.elnix.dragonlauncher.settings.stores.ColorSettingsStore
import org.elnix.dragonlauncher.settings.stores.DebugSettingsStore
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.settings.stores.HoldToActivateArcSettingsStore
import org.elnix.dragonlauncher.settings.stores.LanguageSettingsStore
import org.elnix.dragonlauncher.settings.stores.LegacyFloatingAppsSettingsStore
import org.elnix.dragonlauncher.settings.stores.PrivateAppsSettingsStore
import org.elnix.dragonlauncher.settings.stores.PrivateSettingsStore
import org.elnix.dragonlauncher.settings.stores.StatusBarJsonSettingsStore
import org.elnix.dragonlauncher.settings.stores.StatusBarSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeMapSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.settings.stores.WellbeingSettingsStore
import org.elnix.dragonlauncher.settings.stores.WidgetsSettingsStore
import org.elnix.dragonlauncher.settings.stores.WorkspaceSettingsStore


enum class DataStoreName(
    override val value: String,
    override val backupKey: String,
    override val userBackup: Boolean = true
) : DatastoreProvider {
    UI("uiDatastore", "ui"),
    COLOR_MODE("colorModeDatastore", "color_mode"),
    COLOR("colorDatastore", "color"),
    PRIVATE_SETTINGS("privateSettingsStore", "private", false),
    SWIPE("swipePointsDatastore", "new_actions"),
    LANGUAGE("languageDatastore", "language"),
    DRAWER("drawerDatastore", "drawer"),
    DEBUG("debugDatastore", "debug"),
    WORKSPACES("workspacesDataStore", "workspaces"),
    PRIVATE_APPS("privateAppsDatastore", "private_apps", false),
    BEHAVIOR("behaviorDatastore", "behavior"),
    BACKUP("backupDatastore", "backup"),
    STATUS_BAR("statusDatastore", "status_bar"),
    LEGACY_FLOATING_APPS("floatingAppsDatastore", "floating_apps", false), // No user backup for this one because it's legacy
    WIDGETS("widgetsDatastore", "widgets"),
    WELLBEING("wellbeingDatastore", "wellbeing"),
    SWIPE_MAP("swipeMapDataStore", "swipe_map"),
    STATUS_BAR_JSON("statusBarJsonDataStore", "status_bar_json"),
    ANGLE_LINE("AngleLineDatastore", "angle_line"),
    HOLD_TO_ACTIVATE("HoldTOActivateDatastore", "hold_to_activate")
}


object SettingsStoreRegistry {
    val byName: Map<DatastoreProvider, BaseSettingsStore<*, *>> = mapOf(
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
        LEGACY_FLOATING_APPS to LegacyFloatingAppsSettingsStore,
        WIDGETS to WidgetsSettingsStore,
        WELLBEING to WellbeingSettingsStore,
        SWIPE_MAP to SwipeMapSettingsStore,
        STATUS_BAR_JSON to StatusBarJsonSettingsStore,
        ANGLE_LINE to AngleLineSettingsStore,
        HOLD_TO_ACTIVATE to HoldToActivateArcSettingsStore
    )
}

val allStores: Map<DatastoreProvider, BaseSettingsStore<*, *>>
    get() = SettingsStoreRegistry.byName

val themeStores: Set<DataStoreName>
    get() = setOf(UI, COLOR_MODE, COLOR, ANGLE_LINE, HOLD_TO_ACTIVATE)

val backupableStores: Map<DatastoreProvider, BaseSettingsStore<*, *>>
    get() = SettingsStoreRegistry.byName
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
private val Context.legacyFloatingAppsDatastore by preferencesDataStore(name = LEGACY_FLOATING_APPS.value)
private val Context.widgetsDatastore by preferencesDataStore(name = WIDGETS.value)
private val Context.wellbeingDatastore by preferencesDataStore(name = WELLBEING.value)
private val Context.swipeMapDatastore by preferencesDataStore(name = SWIPE_MAP.value)
private val Context.statusBarJsonDataStore by preferencesDataStore(name = STATUS_BAR_JSON.value)
private val Context.angleLineDatastore by preferencesDataStore(name = ANGLE_LINE.value)
private val Context.holeToActivateDatastore by preferencesDataStore(name = HOLD_TO_ACTIVATE.value)


internal fun Context.resolveDataStore(name: DatastoreProvider): DataStore<Preferences> {
    val appCtx = this.applicationContext
    return when (name) {
        UI -> appCtx.uiDatastore
        COLOR_MODE -> appCtx.colorModeDatastore
        COLOR -> appCtx.colorDatastore
        PRIVATE_SETTINGS -> appCtx.privateSettingsStore
        SWIPE -> appCtx.swipeDataStore
        LANGUAGE -> appCtx.languageDatastore
        DRAWER -> appCtx.drawerDataStore
        DEBUG -> appCtx.debugDatastore
        WORKSPACES -> appCtx.workspaceDataStore
        PRIVATE_APPS -> appCtx.privateAppsDatastore
        BEHAVIOR -> appCtx.behaviorDataStore
        BACKUP -> appCtx.backupDatastore
        STATUS_BAR -> appCtx.statusBarDatastore
        LEGACY_FLOATING_APPS -> appCtx.legacyFloatingAppsDatastore
        WIDGETS -> appCtx.widgetsDatastore
        WELLBEING -> appCtx.wellbeingDatastore
        SWIPE_MAP -> appCtx.swipeMapDatastore
        STATUS_BAR_JSON -> appCtx.statusBarJsonDataStore
        ANGLE_LINE -> appCtx.angleLineDatastore
        HOLD_TO_ACTIVATE -> appCtx.holeToActivateDatastore
        else -> null
    } ?: error("Datastore not found")
}



suspend fun clearAllData(ctx: Context) {
    DataStoreName.entries.forEach { dataStoreName ->
        val datastore = ctx.resolveDataStore(dataStoreName)
        datastore.edit { preferences ->
            preferences.clear()
        }
    }
}