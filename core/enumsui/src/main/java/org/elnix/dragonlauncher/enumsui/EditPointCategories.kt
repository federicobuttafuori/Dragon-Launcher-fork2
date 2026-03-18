package org.elnix.dragonlauncher.enumsui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.ui.graphics.vector.ImageVector
import org.elnix.dragonlauncher.common.R
enum class EditPointCategories(
    override val resId: Int?,
    override val iconEnabled: ImageVector,
    override val iconDisabled: ImageVector?
): ToggleButtonOption {
    Menu(R.string.collapse, Icons.Default.Menu, null),
    General(R.string.general, Icons.Default.Settings, null),
    Action(R.string.action, Icons.Default.PendingActions, null),
    Size(R.string.size, Icons.Default.FormatSize, null),
    Appearance(R.string.appearance, Icons.Default.Palette, null),
    Haptic(R.string.haptic_feedback, Icons.Default.Vibration, null)
}