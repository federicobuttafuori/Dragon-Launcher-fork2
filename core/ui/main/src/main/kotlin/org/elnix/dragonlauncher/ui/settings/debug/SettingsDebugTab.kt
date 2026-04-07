@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.settings.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.utils.copyToClipboard
import org.elnix.dragonlauncher.settings.allStores
import org.elnix.dragonlauncher.ui.components.dragon.DragonIconButton
import org.elnix.dragonlauncher.ui.dialogs.ExportSettingsDialog
import org.elnix.dragonlauncher.ui.helpers.MonospaceScrollableText
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsScaffold
import org.json.JSONObject

@Composable
fun SettingsDebugTab(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    var settingsJson by remember { mutableStateOf<JSONObject?>(null) }

    var selectedStores by remember { mutableStateOf(allStores) }
    var showStoresDialog by remember { mutableStateOf(false) }

    fun loadSettings() {
        settingsJson = null
        scope.launch {
            val json = JSONObject()

            selectedStores.forEach { store ->
                store.value.exportForBackup(ctx)?.let {
                    json.put(store.key.backupKey, it)
                }
            }
            settingsJson = json
        }
    }

    val jsonLines by remember(settingsJson) {
        mutableStateOf(settingsJson?.toString(2)?.lines().orEmpty())
    }

    LaunchedEffect(Unit) {
        loadSettings()
    }

    val listState = rememberLazyListState()

    SettingsScaffold(
        title = "Settings debug json",
        onBack = onBack,
        helpText = "settings json",
        onReset = null,
        resetText = null,
        listState = listState,
        titleContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                Button(
                    onClick = { showStoresDialog = true }
                ) {
                    Text("Select visibles stores")
                }

                Spacer(Modifier.weight(1f))
                DragonIconButton(
                    onClick = { settingsJson?.let { ctx.copyToClipboard(it.toString(2)) } },
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy"
                )

                DragonIconButton(
                    onClick = { loadSettings() },
                    imageVector = Icons.Default.Loop,
                    contentDescription = "Load settings"
                )
            }
        },
        content = {
            MonospaceScrollableText(jsonLines)
        }
    )
    if (showStoresDialog) {
        ExportSettingsDialog(
            onDismiss = { showStoresDialog = false },
            defaultStores = selectedStores,
            availableStores = allStores
        ) {
            selectedStores = it
            showStoresDialog = false
            loadSettings()
        }
    }
}