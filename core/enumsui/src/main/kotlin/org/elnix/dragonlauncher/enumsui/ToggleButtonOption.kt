package org.elnix.dragonlauncher.enumsui

import androidx.compose.ui.graphics.vector.ImageVector

interface ToggleButtonOption {
    val resId: Int?
    val iconEnabled: ImageVector?
    val iconDisabled: ImageVector?
}