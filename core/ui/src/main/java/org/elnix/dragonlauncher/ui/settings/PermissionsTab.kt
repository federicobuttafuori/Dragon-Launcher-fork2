package org.elnix.dragonlauncher.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.utils.Constants.Logging.TAG
import org.elnix.dragonlauncher.ui.helpers.SwitchRow
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader

@Composable
fun PermissionsTab(onBack: () -> Unit) {
    val ctx = LocalContext.current
    val permissionStates = remember { mutableStateMapOf<String, Boolean>() }

    fun checkPermissions() {
        val perms = listOf(
            Manifest.permission.QUERY_ALL_PACKAGES,
            Manifest.permission.REQUEST_DELETE_PACKAGES,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.BIND_APPWIDGET,
        ).let { list ->
            var updatedList = list
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                updatedList = updatedList + Manifest.permission.POST_NOTIFICATIONS
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                updatedList = updatedList + Manifest.permission.SCHEDULE_EXACT_ALARM
            }
            updatedList
        }

        perms.forEach { permission ->
            permissionStates[permission] = ContextCompat.checkSelfPermission(ctx, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    LaunchedEffect(Unit) {
        checkPermissions()
    }

    Column {
        SettingsLazyHeader(
            title = stringResource(R.string.permissions),
            onBack = onBack,
            helpText = "Gérez ici les permissions et accès système pour Dragon Launcher.",
            onReset = null
        ) {
            item {
                Text(
                    text = "Accès système spéciaux",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                SwitchRow(
                    state = null,
                    onCheck = {
                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        ctx.startActivity(intent)
                    },
                    text = "Accès aux notifications",
                    subText = "Permet d'afficher les badges de notification sur les icônes."
                )
            }

            item {
                SwitchRow(
                    state = null,
                    onCheck = {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        ctx.startActivity(intent)
                    },
                    text = "Service d'accessibilité",
                    subText = "Utilisé pour étendre le panneau de notifications via des gestes."
                )
            }

            item {
                SwitchRow(
                    state = null,
                    onCheck = {
                        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                        ctx.startActivity(intent)
                    },
                    text = "Accès aux statistiques d'utilisation",
                    subText = "Requis pour les fonctionnalités de Bien-être numérique."
                )
            }

            item {
                Text(
                    text = "Permissions Android",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            val androidPermissions = listOf(
                Manifest.permission.QUERY_ALL_PACKAGES,
                Manifest.permission.BIND_APPWIDGET,
            ).let { list ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    list + Manifest.permission.POST_NOTIFICATIONS
                } else list
            }

            androidPermissions.forEach { permission ->
                item {
                    val isGranted = permissionStates[permission] ?: false
                    SwitchRow(
                        state = isGranted,
                        onCheck = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", ctx.packageName, null)
                            }
                            ctx.startActivity(intent)
                        },
                        text = permission.substringAfterLast("."),
                        subText = "Gérer dans les paramètres système."
                    )
                }
            }
        }
    }
}
