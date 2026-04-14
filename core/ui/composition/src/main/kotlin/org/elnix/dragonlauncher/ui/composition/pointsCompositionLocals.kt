package org.elnix.dragonlauncher.ui.composition

import androidx.compose.runtime.compositionLocalOf
import org.elnix.dragonlauncher.common.serializables.CacheKey
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.IconsCache

val LocalDrawerIconsCache = compositionLocalOf<IconsCache<CacheKey>> { error("No drawer icons provided") }
val LocalPointIconsCache = compositionLocalOf<IconsCache<String>> { error("No points icons provided") }
val LocalIconShape = compositionLocalOf<IconShape> { error("No iconShape Provided") }
val LocalNests = compositionLocalOf<List<CircleNest>> { error("No nests provided") }
val LocalPoints = compositionLocalOf<List<SwipePointSerializable>> { error("No points provided") }
val LocalDefaultPoint = compositionLocalOf<SwipePointSerializable> { error("No default point provided") }

