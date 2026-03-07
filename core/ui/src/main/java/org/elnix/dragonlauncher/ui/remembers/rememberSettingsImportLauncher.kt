package org.elnix.dragonlauncher.ui.remembers

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.elnix.dragonlauncher.common.logging.logD
import org.json.JSONObject

@Composable
fun rememberSettingsImportLauncher(
    ctx: Context,
    scope: CoroutineScope,
    onCancel: () -> Unit,
    onError: (String) -> Unit,
    onJsonReady: (JSONObject) -> Unit
): ManagedActivityResultLauncher<Array<String>, Uri?> {

    return rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->

        ctx.logD("BackupManager") { "File picked: $uri" }

        if (uri == null) {
            onCancel()
            return@rememberLauncherForActivityResult
        }

        ctx.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )

        scope.launch {
            try {
                val jsonString = withContext(Dispatchers.IO) {
                    ctx.contentResolver
                        .openInputStream(uri)
                        ?.bufferedReader()
                        ?.use { it.readText() }
                }

                if (jsonString.isNullOrBlank()) {
                    onError("Invalid or empty backup file")
                    return@launch
                }

                onJsonReady(JSONObject(jsonString))

            } catch (e: Exception) {
                onError("Failed to read backup file: ${e.message}")
            }
        }
    }
}
