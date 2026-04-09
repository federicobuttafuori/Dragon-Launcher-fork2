package org.elnix.dragonlauncher.enumsui

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.runtime.Composable
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.enumsui.DrawerActions.CLEAR
import org.elnix.dragonlauncher.enumsui.DrawerActions.CLOSE
import org.elnix.dragonlauncher.enumsui.DrawerActions.CLOSE_KB
import org.elnix.dragonlauncher.enumsui.DrawerActions.DISABLED
import org.elnix.dragonlauncher.enumsui.DrawerActions.NONE
import org.elnix.dragonlauncher.enumsui.DrawerActions.OPEN_FIRST_APP
import org.elnix.dragonlauncher.enumsui.DrawerActions.OPEN_KB
import org.elnix.dragonlauncher.enumsui.DrawerActions.SEARCH_WEB
import org.elnix.dragonlauncher.enumsui.DrawerActions.TOGGLE_KB


enum class DrawerActions { CLOSE, CLEAR, TOGGLE_KB, CLOSE_KB, OPEN_KB, SEARCH_WEB, OPEN_FIRST_APP, NONE, DISABLED }

@Composable
fun drawerActionIcon(action: DrawerActions) = when (action) {
    CLOSE -> Icons.Default.Close
    TOGGLE_KB -> Icons.Default.Keyboard
    NONE -> Icons.Default.RadioButtonUnchecked
    DISABLED -> Icons.Default.RadioButtonUnchecked
    CLEAR -> Icons.Default.ClearAll
    SEARCH_WEB -> Icons.Default.Language
    OPEN_FIRST_APP -> Icons.AutoMirrored.Filled.OpenInNew
    CLOSE_KB -> Icons.Default.Keyboard
    OPEN_KB -> Icons.Default.Keyboard
}

fun drawerActionsLabel(ctx: Context,action: DrawerActions) = when (action) {
    CLOSE -> ctx.getString(R.string.close_app_drawer)
    TOGGLE_KB -> ctx.getString(R.string.toggle_kb)
    NONE -> ctx.getString(R.string.none)
    DISABLED -> ""
    CLEAR -> ctx.getString(R.string.drawer_action_clear)
    SEARCH_WEB -> ctx.getString(R.string.drawer_action_search_web)
    OPEN_FIRST_APP -> ctx.getString(R.string.drawer_action_open_first_app)
    CLOSE_KB -> ctx.getString(R.string.close_kb)
        OPEN_KB -> ctx.getString(R.string.open_kb)
}

fun DrawerActions.isUsed(): Boolean =
    this != DISABLED && this != NONE
