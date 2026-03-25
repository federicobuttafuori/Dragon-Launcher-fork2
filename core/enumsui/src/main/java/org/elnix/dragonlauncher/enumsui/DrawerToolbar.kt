package org.elnix.dragonlauncher.enumsui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.RecentActors
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.enumsui.DrawerToolbar.RecentlyUsed
import org.elnix.dragonlauncher.enumsui.DrawerToolbar.SearchBar
import org.elnix.dragonlauncher.enumsui.DrawerToolbar.Spacer

enum class DrawerToolbar(
//    val show: Boolean,
//    val resId: Int,
//    val icon: ImageVector,
//    val height: Int
) {
    Spacer,//(true, R.string.spacer, Icons.Default.Height, 0),
    RecentlyUsed,//(true, R.string.recently_used_apps, Icons.Default.RecentActors, 80),
    SearchBar,//&(true, R.string.search_bar, Icons.Default.Search, 60)
}


val DrawerToolbar.resId: Int
    get() = when (this) {
        Spacer -> R.string.spacer
        RecentlyUsed -> R.string.recently_used_apps
        SearchBar -> R.string.search_bar
    }

val DrawerToolbar.height: Int
    get() = when (this) {
        Spacer -> 0
        RecentlyUsed -> 90
        SearchBar -> 60
    }

val DrawerToolbar.icon: ImageVector
    get() = when (this) {
        Spacer -> Icons.Default.Height
        RecentlyUsed -> Icons.Default.RecentActors
        SearchBar -> Icons.Default.Search
    }

