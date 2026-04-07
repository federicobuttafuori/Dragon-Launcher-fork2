package org.elnix.dragonlauncher.ui.helpers

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.settings.stores.PrivateSettingsStore
import org.elnix.dragonlauncher.ui.dragon.components.DragonIconButton


@Composable
fun SetDefaultLauncherBanner() {

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .clickable { ctx.startActivity(Intent(Settings.ACTION_HOME_SETTINGS)) }
            .padding(5.dp)
    ) {
        Text(
            stringResource(R.string.set_default_launcher),
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.weight(1f)
        )

        DragonIconButton(
            onClick = {
                scope.launch { PrivateSettingsStore.showSetDefaultLauncherBanner.set(ctx, false) }
            },
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.close)
        )
    }
}
