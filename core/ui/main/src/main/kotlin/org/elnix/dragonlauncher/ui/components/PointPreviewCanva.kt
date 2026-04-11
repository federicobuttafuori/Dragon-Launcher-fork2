package org.elnix.dragonlauncher.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.ui.helpers.nests.actionsInCircle
import org.elnix.dragonlauncher.ui.remembers.rememberSwipeDefaultParams

@Composable
fun PointPreviewCanvas(
    editPoint: SwipePointSerializable,
    defaultPoint: SwipePointSerializable,
    backgroundSurfaceColor: Color,
    modifier: Modifier = Modifier,
    icons: Map<String, ImageBitmap>? = null,
) {
    val drawParams = rememberSwipeDefaultParams(
        icons = icons,
        defaultPointSerializable = defaultPoint,
        backgroundColor = backgroundSurfaceColor
    )

    Canvas(
        modifier = modifier
            .height(40.dp)
    ) {
        val centerY = size.height / 2f
        val leftX = size.width * 0.25f
        val rightX = size.width * 0.75f

        // Left action
        actionsInCircle(
            selected = false,
            point = editPoint,
            center = Offset(leftX, centerY),
            depth = 1,
            drawParams = drawParams,
            preventBgErasing = true,
            showConfiguratorDecorations = true,
        )

        // Right action
        actionsInCircle(
            selected = true,
            point = editPoint,
            center = Offset(rightX, centerY),
            depth = 1,
            drawParams = drawParams,
            preventBgErasing = true,
            showConfiguratorDecorations = true,
        )
    }
}
