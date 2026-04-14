package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.common.utils.copyToClipboard
import org.elnix.dragonlauncher.ui.base.UiConstants.DragonShape

@Composable
fun AppModelInfoDialog(
    app: AppModel,
    onDismiss: () -> Unit
) {
    val ctx = LocalContext.current

    AlertDialog(
        text = {
            Column {
                Text(
                    text = stringResource(R.string.app_info_name, app.name),
                    modifier = Modifier.clickable { ctx.copyToClipboard(app.name) }
                )
                Text(
                    text = stringResource(R.string.app_info_package_name, app.packageName),
                    modifier = Modifier.clickable { ctx.copyToClipboard(app.packageName) }
                )
                Text(text = stringResource(R.string.app_info_is_enabled, app.isEnabled.toString()))
                Text(text = stringResource(R.string.app_info_is_system, app.isSystem.toString()))
                Text(text = stringResource(R.string.app_info_is_work_profile, app.isWorkProfile.toString()))
                Text(text = stringResource(R.string.app_info_is_private_profile, app.isPrivateProfile.toString()))
                Text(text = stringResource(R.string.app_info_is_launchable, app.isLaunchable.toString()))
                Text(text = stringResource(R.string.app_info_user_id, app.userId.toString()))
                Text(text = stringResource(R.string.app_info_cache_key, app.iconCacheKey.cacheKey))
            }
        },
        dismissButton = {},
        confirmButton = {},
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shape = DragonShape
    )
}
