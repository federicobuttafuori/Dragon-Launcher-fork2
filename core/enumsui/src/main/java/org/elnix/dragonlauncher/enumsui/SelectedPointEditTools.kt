package org.elnix.dragonlauncher.enumsui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.ui.graphics.vector.ImageVector
import org.elnix.dragonlauncher.common.R

enum class SelectedPointEditTools(
    override val resId: Int,
    override val iconEnabled: ImageVector,
    override val iconDisabled: ImageVector?
) : ToggleButtonOption {
    Edit(R.string.edit_point, Icons.Default.Edit, null),
    Remove(R.string.remove_point, Icons.Default.Remove, null),
    Duplicate(R.string.copy_point, Icons.Default.ContentCopy, null)
}