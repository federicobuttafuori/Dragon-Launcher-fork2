package org.elnix.dragonlauncher.ui.helpers.nests

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.UiCircle
import org.elnix.dragonlauncher.common.utils.circles.computePointPosition
import org.elnix.dragonlauncher.ui.helpers.nests.points.SwipeDrawParams

fun DrawScope.circlesSettingsOverlay(
    drawParams: SwipeDrawParams,


    center: Offset,
    depth: Int,

    circles: List<UiCircle>,
    selectedPoint: SwipePointSerializable?,
    nestId: Int,
    selectedAll: Boolean = false,
    preventBgErasing: Boolean = false,
    /** When true, only the [selectedPoint] draws cycle stack + Hold & Run bolt (nest editor / dialogs). */
    showConfiguratorDecorations: Boolean = false,
) {

    val points = drawParams.points
    val surfaceColorDraw = drawParams.surfaceColorDraw
    val extraColors = drawParams.extraColors
    val showCircle = drawParams.showCircle

    /* ───────────── Erases the circle in the point ───────────── */

    // if no background color provided, erases the background
    val eraseBg = surfaceColorDraw == Color.Transparent && !preventBgErasing
    val maxCircleSize: UiCircle = circles.maxByOrNull { it.radius } ?: return

    val currentNest = drawParams.nests.find { it.id == nestId } ?: return

    // Erases the color, instead of putting it, that lets the wallpaper pass through
    // Always to it to remove the remaining circle line behind previous points (nests for instance that have their inner circle empty)
    drawCircle(
        color = Color.Transparent,
        radius = maxCircleSize.radius,
        center = center,
        blendMode = BlendMode.Clear
    )

    // If requested to not erase the bg, draw it (this avoids the more tinted bg when using a half transparent bg color
    if (!eraseBg) {
        drawCircle(
            color = surfaceColorDraw,
            radius = maxCircleSize.radius,
            center = center
        )
    }

    // 1. Draw all circles
    circles.forEach { circle ->
        if (currentNest.showCircle ?: showCircle) {
            drawCircle(
                color = extraColors.circle,
                radius = circle.radius,
                center = center,
                style = Stroke(if (selectedAll) 8f else 4f)
            )
        }


        // 2. Draw all points that belongs to the actual circle, selected last
        points
            .filter {
                it.circleNumber == circle.id &&
                it.nestId == nestId
            }
            .sortedBy { it.id == selectedPoint?.id }
            .forEach { p ->

                val newCenter = computePointPosition(
                    point = p,
                    circles = circles,
                    center = center
                )

                // Use the selectedPoint snapshot for the selected point so any staged action
                // from Cycle Actions is reflected visually (e.g. different nest or app icon).
                val drawPoint = if (selectedPoint != null && p.id == selectedPoint.id)
                    selectedPoint else p

                val decorate =
                    showConfiguratorDecorations &&
                        selectedPoint != null &&
                        p.id == selectedPoint.id

                actionsInCircle(
                    drawParams = drawParams,

                    center = newCenter,
                    depth = depth,
                    point = drawPoint,
                    selected = selectedAll || (p.id == selectedPoint?.id),
                    preventBgErasing = preventBgErasing,
                    showConfiguratorDecorations = decorate,
                )
            }
    }
}
