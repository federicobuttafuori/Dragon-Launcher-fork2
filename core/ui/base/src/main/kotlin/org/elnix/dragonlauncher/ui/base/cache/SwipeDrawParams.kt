package org.elnix.dragonlauncher.ui.base.cache

import android.content.Context
import androidx.compose.ui.graphics.Color
import org.elnix.dragonlauncher.base.theme.ExtraColors
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.IconsCache

data class SwipeDrawParams(
    val nests: List<CircleNest>,
    val points: List<SwipePointSerializable>,
    val ctx: Context,
    val defaultPoint: SwipePointSerializable,
    val icons: IconsCache<String>,
    val surfaceColorDraw: Color,
    val extraColors: ExtraColors,
    val showCircle: Boolean,
    val maxDepth: Int,
    val iconShape: IconShape,
    val subNestDefaultRadius: Float,
    val drawPathCache: DrawPathCache
)
