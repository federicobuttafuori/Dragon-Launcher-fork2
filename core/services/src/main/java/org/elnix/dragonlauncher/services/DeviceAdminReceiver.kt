package org.elnix.dragonlauncher.services

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import org.elnix.dragonlauncher.common.logging.logD

class DeviceAdminReceiver : DeviceAdminReceiver() {

    /** Called when admin is enabled - ACTION_DEVICE_ADMIN_ENABLED */
    override fun onEnabled(context: Context, intent: Intent) {
        logD("DeviceAdmin") { "Dragon Launcher admin enabled successfully" }
    }

    /** Called when admin is disabled - ACTION_DEVICE_ADMIN_DISABLE_REQUESTED */
    override fun onDisabled(context: Context, intent: Intent) {
        logD("DeviceAdmin") { "Dragon Launcher admin disabled" }
    }

    /** Optional: Warn user before disabling */
    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        return "Disabling may cause app to be killed by battery optimization"
    }
}
