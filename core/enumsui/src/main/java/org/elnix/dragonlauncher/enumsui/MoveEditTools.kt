package org.elnix.dragonlauncher.enumsui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.ui.graphics.vector.ImageVector
import org.elnix.dragonlauncher.common.R

enum class MoveEditTools(
    override val resId: Int?,
    override val iconEnabled: ImageVector,
    override val iconDisabled: ImageVector?
) : ToggleButtonOption {
    MoveLeft(R.string.move_point_clockwise, Icons.Default.ChevronLeft, null),
    MoveRight(R.string.move_point_anticlockwise, Icons.Default.ChevronRight, null)
}
