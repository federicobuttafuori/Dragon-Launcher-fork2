package org.elnix.dragonlauncher.ui.statusbar

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.elnix.dragonlauncher.common.serializables.StatusBarSerializable

@Composable
fun StatusBarConnectivity(
    element: StatusBarSerializable.Connectivity,
    modifier: Modifier = Modifier,
    previewMode: Boolean = false
) {
    val ctx = LocalContext.current
    var connectivityState by remember {
        mutableStateOf(
            if (previewMode) ConnectivityState(
                isWifiEnabled = true,
                isBluetoothEnabled = true,
                isMobileDataEnabled = true
            ) else ConnectivityState()
        )
    }

    // Periodic updates
    if (!previewMode) {
        LaunchedEffect(element.updateFrequency) {
            while (true) {
                connectivityState = readConnectivityState(ctx)
                delay(element.updateFrequency * 1000L)
            }
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (connectivityState.isAirplaneMode && element.showAirplaneMode) {
            Icon(
                imageVector = Icons.Filled.AirplanemodeActive,
                contentDescription = "Airplane",
                modifier = Modifier.size(14.dp)
            )
        } else {
            if (connectivityState.isWifiEnabled && element.showWifi) {
                Icon(
                    imageVector = Icons.Filled.Wifi,
                    contentDescription = "WiFi on",
                    modifier = Modifier.size(14.dp)
                )
            }

            if (connectivityState.isBluetoothEnabled && element.showBluetooth) {
                Icon(
                    imageVector = Icons.Filled.Bluetooth,
                    contentDescription = "Bluetooth",
                    modifier = Modifier.size(14.dp)
                )
            }

            if (connectivityState.isVpnEnabled && element.showVpn) {
                Icon(
                    imageVector = Icons.Filled.VpnKey,
                    contentDescription = "VPN",
                    modifier = Modifier.size(14.dp)
                )
            }

            if (connectivityState.isMobileDataEnabled && element.showMobileData) {
                Icon(
                    imageVector = Icons.Filled.SignalCellularAlt,
                    contentDescription = connectivityState.mobileDataStatus,
                    modifier = Modifier.size(14.dp)
                )
            }

            if (connectivityState.isHotspotEnabled && element.showHotspot) {
                Icon(
                    imageVector = Icons.Filled.WifiTethering,
                    contentDescription = "Hotspot",
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

data class ConnectivityState(
    val isAirplaneMode: Boolean = false,
    val isWifiEnabled: Boolean = false,
    val isVpnEnabled: Boolean = false,
    val isBluetoothEnabled: Boolean = false,
    val isHotspotEnabled: Boolean = false,
    val isMobileDataEnabled: Boolean = false,
    val mobileDataStatus: String = ""
)

private fun readConnectivityState(ctx: Context): ConnectivityState {
    val resolver = ctx.contentResolver
    val connectivityManager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val (mobileDataEnabled, mobileDataStatus) = getMobileDataStatus(ctx, connectivityManager, resolver)

    val isVpnEnabled = connectivityManager.allNetworks.any { network ->
        connectivityManager.getNetworkCapabilities(network)?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true
    }

    return ConnectivityState(
        isAirplaneMode = Settings.Global.getInt(resolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 1,

        isWifiEnabled = when {
            Settings.Global.getInt(resolver, Settings.Global.WIFI_ON, 0) == 1 -> true
            Settings.Global.getInt(resolver, "wifi_on", 0) == 1 -> true
            else -> false
        },

        isVpnEnabled = isVpnEnabled,

        isBluetoothEnabled = Settings.Global.getInt(resolver, Settings.Global.BLUETOOTH_ON, 0) == 1,
        isHotspotEnabled = Settings.Global.getInt(resolver, "wifi_ap_state", 0) == 13,
        isMobileDataEnabled = mobileDataEnabled,
        mobileDataStatus = mobileDataStatus
    )
}

private fun getMobileDataStatus(
    ctx: Context,
    connectivityManager: ConnectivityManager,
    resolver: android.content.ContentResolver
): Pair<Boolean, String> {
    /*  ─────────────  Mobile data status  ─────────────  */
    // 1. Check if mobile data is enabled (check multiple SIMs)
    val mobileDataEnabled = try {
        Settings.Global.getInt(resolver, "mobile_data", 0) == 1 ||
                Settings.Global.getInt(resolver, "mobile_data1", 0) == 1 ||
                Settings.Global.getInt(resolver, "mobile_data2", 0) == 1
    } catch (e: Exception) {
        true // Default to enabled if unable to access
    }

    if (!mobileDataEnabled) return false to "Data OFF"

    // 2. Get active cellular network + signal
    val activeNetwork = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

    if (capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true) {
        // For now, just return network type without signal strength access might require additional permissions
        val telephonyManager = ctx.getSystemService(Context.TELEPHONY_SERVICE) as android.telephony.TelephonyManager
        val networkType = try {
            telephonyManager.dataNetworkType
        } catch (e: Exception) {
            android.telephony.TelephonyManager.NETWORK_TYPE_UNKNOWN
        }

        val typeStr = when (networkType) {
            android.telephony.TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
            20 -> "5G" // TelephonyManager.NETWORK_TYPE_NR = 20
            android.telephony.TelephonyManager.NETWORK_TYPE_HSDPA, android.telephony.TelephonyManager.NETWORK_TYPE_HSUPA -> "3G"
            else -> "2G"
        }

        val isRoaming = try {
            telephonyManager.isNetworkRoaming
        } catch (e: Exception) {
            false
        }

        return true to (if (isRoaming) "$typeStr (Roaming)" else typeStr)
    }

    return true to "Data ON"
}
