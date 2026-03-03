package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.enumsui.LockMethod
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object PrivateSettingsStore : MapSettingsStore() {

    override val name: String = "Private"
    override val dataStoreName: DataStoreName = DataStoreName.PRIVATE_SETTINGS

    val hasSeenWelcome = Settings.boolean(
        key = "hasSeenWelcome",
        dataStoreName = dataStoreName,
        default = false
    )

    val hasInitialized = Settings.boolean(
        key = "hasInitialized",
        dataStoreName = dataStoreName,
        default = false
    )

    val showSetDefaultLauncherBanner = Settings.boolean(
        key = "showSetDefaultLauncherBanner",
        dataStoreName = dataStoreName,
        default = true
    )

    val lastSeenVersionCodeWhatsNew = Settings.int(
        key = "lastSeenVersionCode",
        dataStoreName = dataStoreName,
        default = 0,
        allowedRange = 0..Int.MAX_VALUE
    )

    val lastSeenVersionCodeGoogleLockdownWarning = Settings.int(
        key = "lastSeenVersionCodeGoogleLockdownWarning",
        dataStoreName = dataStoreName,
        default = 0,
        allowedRange = 0..Int.MAX_VALUE
    )

    /** Hashed PIN for settings lock (SHA-256). Empty string means no PIN set. */
    val lockPinHash = Settings.string(
        key = "lockPinHash",
        dataStoreName = dataStoreName,
        default = ""
    )

    val lockMethod = Settings.enum(
        key = "lockMethod",
        dataStoreName = dataStoreName,
        default = LockMethod.NONE,
        enumClass = LockMethod::class.java
    )

    val samsungPreferSecureFolder = Settings.boolean(
        key = "samsung_prefer_secure_folder",
        dataStoreName = dataStoreName,
        default = false
    )

    val lastBackupTime = Settings.long(
        key = "lastBackupTime",
        dataStoreName = dataStoreName,
        default = System.currentTimeMillis(),
        allowedRange = Long.MIN_VALUE..Long.MAX_VALUE
    )

    override val ALL: List<BaseSettingObject<*,*>> = listOf(
        hasSeenWelcome,
        hasInitialized,
        showSetDefaultLauncherBanner,
        lastSeenVersionCodeWhatsNew,
        lockPinHash,
        lockMethod,
        samsungPreferSecureFolder,
        lastBackupTime
    )
}
