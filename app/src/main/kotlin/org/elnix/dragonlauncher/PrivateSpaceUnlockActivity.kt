package org.elnix.dragonlauncher

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.settings.stores.DebugSettingsStore
import org.elnix.dragonlauncher.ui.base.asState
import org.elnix.dragonlauncher.ui.helpers.PrivateSpaceStateDebugScreen
import org.elnix.dragonlauncher.ui.helpers.PrivateSpaceUnlockScreen
import org.elnix.dragonlauncher.ui.theme.DragonLauncherTheme

class PrivateSpaceUnlockActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val ctx = LocalContext.current
            val appsViewModel = remember(ctx) {
                (ctx.applicationContext as DragonLauncherApplication).appsViewModel
            }

            DragonLauncherTheme {

                PrivateSpaceUnlockScreen(
                    onCancel = { finish() },
                    onStart = { scope ->
                        scope.launch {
                            appsViewModel.unlockAndReloadPrivateSpace()
                            finish()
                        }
                    }
                )

                val privateSpaceState by appsViewModel.privateSpaceState.collectAsState()

                val privateSpaceDebugInfo by DebugSettingsStore.privateSpaceDebugInfo.asState()
                AnimatedVisibility(privateSpaceDebugInfo) {
                    PrivateSpaceStateDebugScreen(privateSpaceState)
                }
            }
        }
    }
}
