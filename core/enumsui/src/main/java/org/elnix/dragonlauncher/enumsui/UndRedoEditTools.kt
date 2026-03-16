package org.elnix.dragonlauncher.enumsui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.ui.graphics.vector.ImageVector
import org.elnix.dragonlauncher.common.R

enum class UndRedoEditTools(
    override val resId: Int?,
    override val iconEnabled: ImageVector,
    override val iconDisabled: ImageVector?
) : ToggleButtonOption {
    UndoAll(R.string.undo_all, Icons.Default.KeyboardDoubleArrowLeft, null),
    Undo(R.string.undo, Icons.AutoMirrored.Filled.Undo, null),
    Redo(R.string.redo, Icons.AutoMirrored.Filled.Redo, null),
    RedoAll(R.string.redo_all, Icons.Default.KeyboardDoubleArrowRight, null)
}
