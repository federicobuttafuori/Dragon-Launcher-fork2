package org.elnix.dragonlauncher.common.points

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import org.elnix.dragonlauncher.base.theme.ExtraColors
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable

data class SwipeDrawParams(
    val nests: List<CircleNest>,
    val points: List<SwipePointSerializable>,
    val ctx: Context,
    val defaultPoint: SwipePointSerializable,
    val icons: Map<String, ImageBitmap>,
    val surfaceColorDraw: Color,
    val extraColors: ExtraColors,
    val showCircle: Boolean,
    val maxDepth: Int,
    val iconShape: IconShape,
    val subNestDefaultRadius: Float,
    val drawPathCache: DrawPathCache
)
