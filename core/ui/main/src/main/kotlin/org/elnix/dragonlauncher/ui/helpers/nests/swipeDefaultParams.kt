package org.elnix.dragonlauncher.ui.helpers.nests

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.base.ktx.px
import org.elnix.dragonlauncher.base.theme.LocalExtraColors
import org.elnix.dragonlauncher.common.points.DrawPathCache
import org.elnix.dragonlauncher.common.points.SwipeDrawParams
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.settings.stores.SwipeMapSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.base.asState
import org.elnix.dragonlauncher.ui.remembers.LocalDefaultPoint
import org.elnix.dragonlauncher.ui.remembers.LocalIconShape
import org.elnix.dragonlauncher.ui.remembers.LocalIcons
import org.elnix.dragonlauncher.ui.remembers.LocalNests
import org.elnix.dragonlauncher.ui.remembers.LocalPoints


@Composable
fun swipeDefaultParams(
    backgroundColor: Color? = null,
    points: List<SwipePointSerializable>? = null,
    nests: List<CircleNest>? = null,
    icons: Map<String, ImageBitmap>? = null,
    defaultPointSerializable: SwipePointSerializable? = null,
    showCircle: Boolean? = null
): SwipeDrawParams {
    val ctx = LocalContext.current
    val points = points ?: LocalPoints.current
    val defaultPointSettings = LocalDefaultPoint.current
    val nests = nests ?: LocalNests.current
    val icons = icons ?: LocalIcons.current
    val iconShape = LocalIconShape.current
    val extraColors = LocalExtraColors.current

    val surfaceColorDraw = backgroundColor ?: Color.Unspecified

    val defaultPoint = defaultPointSerializable ?: defaultPointSettings

    val showCircleSetting by UiSettingsStore.showCirclePreview.asState()
    val showCircle = showCircle ?: showCircleSetting

    val maxNestsDepth by UiSettingsStore.maxNestsDepth.asState()

    val subNestDefaultRadius by SwipeMapSettingsStore.subNestDefaultRadius.asState()

    val drawPathCache = remember { DrawPathCache(points.size) }

    LaunchedEffect(points.size) {
        drawPathCache.updateMaxCacheSize(points.size)
    }

    return SwipeDrawParams(
        nests = nests,
        points = points,
        ctx = ctx,
        defaultPoint = defaultPoint,
        icons = icons,
        surfaceColorDraw = surfaceColorDraw,
        extraColors = extraColors,
        showCircle = showCircle,
        maxDepth = maxNestsDepth,
        iconShape = iconShape,
        subNestDefaultRadius = subNestDefaultRadius.dp.px,
        drawPathCache = drawPathCache
    )
}
