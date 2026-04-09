package org.elnix.dragonlauncher.ui.settings.debug

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.utils.copyToClipboard
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.ui.composition.LocalDragonLogViewModel
import org.elnix.dragonlauncher.ui.helpers.MonospaceScrollableText
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsScaffold
import java.io.File

@Composable
fun LogsViewerScreen(
    filename: String,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val dragonLogViewModel = LocalDragonLogViewModel.current

    val file = File(ctx.filesDir, "logs/$filename")

    var logs: String by remember(filename) { mutableStateOf("") }
    LaunchedEffect(Unit) {
        logs = dragonLogViewModel.readLogFile(file)
    }

    SettingsScaffold(
        title = filename,
        onBack = onBack,
        helpText = "View logs from the log file: $filename",
        onReset = null,
        resetText = null,
        otherIcons = arrayOf(
            Triple(
                { ctx.copyToClipboard(logs); ctx.showToast("Copied to clipboard") },
                Icons.Default.ContentCopy,
                stringResource(R.string.copy)
            )
        ),
        content = {
            MonospaceScrollableText(lines = logs.lines(), useDragonLogsColoration = true)
        }
    )
}