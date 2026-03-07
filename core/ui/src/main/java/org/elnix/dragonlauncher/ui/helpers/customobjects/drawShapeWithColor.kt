package org.elnix.dragonlauncher.ui.helpers.customobjects

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.LayoutDirection
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.utils.Constants.Logging.ANGLE_LINE_TAG

fun DrawScope.drawShapeWithColor(
    shape: Shape,
    center: Offset,
    size: Size,
    color: Color,
    strokeWidth: Float,
    erase: Boolean = false
) {
    val outline = shape.createOutline(
        size = size,
        layoutDirection = LayoutDirection.Ltr,
        density = this
    )

    val path = when (outline) {
        is Outline.Rectangle -> {
            Path().apply { addRect(outline.rect) }
        }

        is Outline.Rounded -> {
            Path().apply { addRoundRect(outline.roundRect) }
        }

        is Outline.Generic -> outline.path
    }

    val translatedPath = Path().apply {
        addPath(path)
        translate(center - Offset(size.width / 2, size.height / 2))
    }

    logD(ANGLE_LINE_TAG) { "erase: $erase, strokeWidth: $strokeWidth" }

    if (erase) {
        drawPath(
            path = translatedPath,
            color = Color.Transparent,
            style = Fill,
            blendMode = BlendMode.Clear
        )
    }

    drawPath(
        path = translatedPath,
        color = color,
        style = if (strokeWidth > 0f)
            Stroke(strokeWidth)
        else
            Fill
    )
}
