package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore
import org.elnix.dragonlauncher.settings.bases.Settings

object BackupSettingsStore : MapSettingsStore() {

    override val name: String = "Backup"
    override val dataStoreName = DataStoreName.BACKUP

    override val ALL: List<BaseSettingObject <*, *> >
        get() = listOf(
            this.autoBackupEnabled,
            this.autoBackupUri,
            this.backupStores
        )


    // Because it caused crash at runtime due to early .entries initialization
    private val defaultBackupStores: Set<String>
        get() = DataStoreName.entries
            .filter { it.userBackup }
            .map { it.value }
            .toSet()



    val autoBackupEnabled = Settings.boolean(
        key = "autoBackupEnabled",
        dataStoreName = dataStoreName,
        default = false
    )

    val autoBackupUri = Settings.string(
        key = "autoBackupUri",
        dataStoreName = dataStoreName,
        default = ""
    )

    val backupStores = Settings.stringSet(
        key = "backupStores",
        dataStoreName = dataStoreName,
        default = defaultBackupStores
    )

    // TODO ( after  3.0.0 )
    val numberOfBackupsToKeep = Settings.int(
        key = "numberOfBackupsToKeep",
        dataStoreName = dataStoreName,
        default = 2,
        allowedRange = 1..10
    )
}
