package org.elnix.dragonlauncher.enumsui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.graphics.vector.ImageVector
import org.elnix.dragonlauncher.common.R

enum class ExportImportTheme(
    override val resId: Int?,
    override val iconEnabled: ImageVector,
    override val iconDisabled: ImageVector? = null
) : ToggleButtonOption {
    Export(R.string.export, Icons.Default.Share),
    Import(R.string.import_text, Icons.Default.Download)
}
