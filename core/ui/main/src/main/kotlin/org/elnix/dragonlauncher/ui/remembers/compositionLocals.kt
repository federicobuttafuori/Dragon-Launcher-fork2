package org.elnix.dragonlauncher.ui.remembers

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.navigation.NavHostController
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.CustomObjectSerializable
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.common.serializables.MainScreenLayer
import org.elnix.dragonlauncher.common.serializables.StatusBarSerializable
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.models.AppLifecycleViewModel
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.models.BackupViewModel
import org.elnix.dragonlauncher.models.FloatingAppsViewModel
import org.elnix.dragonlauncher.models.ShizukuViewModel

val LocalIcons = compositionLocalOf<Map<String, ImageBitmap>> { error("No icons provided") }
val LocalIconShape = compositionLocalOf<IconShape> { error("No iconShape Provided") }
val LocalNests = compositionLocalOf<List<CircleNest>> { error("No nests provided") }
val LocalPoints = compositionLocalOf<List<SwipePointSerializable>> { error("No points provided") }
val LocalDefaultPoint = compositionLocalOf<SwipePointSerializable> { error("No default point provided") }
val LocalStatusBarElements = compositionLocalOf<List<StatusBarSerializable>> {
    error("No status bar elements provided")
}


// ViewModels
val LocalAppsViewModel = compositionLocalOf<AppsViewModel> {
    error("No AppsViewModel bar provided")
}
val LocalAppLifecycleViewModel = compositionLocalOf<AppLifecycleViewModel> {
    error("No AppLifecycleViewModel bar provided")
}
val LocalBackupViewModel = compositionLocalOf<BackupViewModel> {
    error("No BackupViewModel bar provided")
}
val LocalFloatingAppsViewModel = compositionLocalOf<FloatingAppsViewModel> {
    error("No FloatingAppsViewModel bar provided")
}
val LocalShizukuViewModel = compositionLocalOf<ShizukuViewModel> {
    error("No LocalShizukuViewModel bar provided")
}


val LocalLineObject = compositionLocalOf<CustomObjectSerializable> {
    error("No LocalLine provided")
}

val LocalAngleLineObject = compositionLocalOf<CustomObjectSerializable> {
    error("No LocalAngleLine provided")
}

val LocalStartLineObject = compositionLocalOf<CustomObjectSerializable> {
    error("No LocalStartLine provided")
}

val LocalEndLineObject = compositionLocalOf<CustomObjectSerializable> {
    error("No LocalEndLine provided")
}

val LocalUseCustomColorChannels = compositionLocalOf<Boolean> {
    error("No LocalUseCustomColorChannels provided")
}

val LocalHoldCustomObject = compositionLocalOf<CustomObjectSerializable> {
    error("No LocalHoldCustomObject provided")
}

val LocalMainScreenLayers = compositionLocalOf<List<MainScreenLayer>> {
    error("No LocalMainScreenLayers provided")
}

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No LocalNavController provided")
}


val LocalShowLabelsInAddPointDialog = compositionLocalOf<Boolean> {
    error("No LocalShowLabelsInAddPointDialog provided")
}
