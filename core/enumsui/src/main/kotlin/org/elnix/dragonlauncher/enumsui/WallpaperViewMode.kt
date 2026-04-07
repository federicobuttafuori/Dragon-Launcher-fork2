package org.elnix.dragonlauncher.enumsui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import org.elnix.dragonlauncher.common.R


enum class WallpaperEditMode(
    override val resId: Int?,
    override val iconEnabled: ImageVector,
    override val iconDisabled: ImageVector? = null
) : ToggleButtonOption {
    Main(R.string.main_screen, Icons.Default.Home),
    Drawer(R.string.drawer_screen, Icons.Default.Apps)
}