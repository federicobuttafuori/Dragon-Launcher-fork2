package org.elnix.dragonlauncher.ui.helpers.nests

import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp


fun DrawScope.glowOverlay(
    center: Offset,
    color: Color,
    radius: Float
) {
    val radius = radius.coerceAtLeast(1f)
    drawCircle(
        brush = Brush.radialGradient(
            0.0f to color,
            1.0f to Color.Transparent,
            center = center,
            radius = radius.dp.toPx()
            ),
        radius = radius.dp.toPx(),
        center = center
    )
}



fun DrawScope.drawNeonGlowLine(
    start: Offset,
    end: Offset,
    color: Color,
    lineStrokeWidth: Float,
    glowRadius: Float,
    glowColor: Color,
    erase: Boolean
) {
    val strokePx = lineStrokeWidth.dp.toPx()

    val glowPx = glowRadius.dp.toPx()

    // Glow overlay (behind)
    if (glowPx > 0f) {
        drawIntoCanvas { canvas ->
            val frameworkPaint = customGlowPain(glowColor, glowPx)

            canvas.nativeCanvas.drawLine(
                start.x,
                start.y,
                end.x,
                end.y,
                frameworkPaint
            )
        }
    }

    if (erase) {
        drawLine(
            color = Color.Transparent,
            start = start,
            end = end,
            strokeWidth = strokePx,
            cap = StrokeCap.Round,
            blendMode = BlendMode.Clear
        )
    }

    // Sharp center line
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokePx,
        cap = StrokeCap.Round
    )
}

fun DrawScope.drawNeonGlowArc(
    topLeft: Offset,
    size: androidx.compose.ui.geometry.Size,
    startAngle: Float,
    sweepAngle: Float,
    color: Color,
    lineStrokeWidth: Float,
    glowRadius: Float,
    glowColor: Color?,
    erase: Boolean
) {
    val strokePx = lineStrokeWidth.dp.toPx()
    val glowPx = glowRadius.dp.toPx()

    val left = topLeft.x
    val top = topLeft.y
    val right = left + size.width
    val bottom = top + size.height

    // Glow overlay
    if (glowPx > 0f) {
        drawIntoCanvas { canvas ->
            val frameworkPaint = customGlowPain(glowColor ?: color, glowPx)

            canvas.nativeCanvas.drawArc(
                left,
                top,
                right,
                bottom,
                startAngle,
                sweepAngle,
                false,
                frameworkPaint
            )
        }
    }

    if (erase) {
        drawArc(
            color = Color.Transparent,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = size,
            style = Stroke(
                width = strokePx,
                cap = StrokeCap.Round
            ),
            blendMode = BlendMode.Clear
        )
    }

    // Sharp arc
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = topLeft,
        size = size,
        style = Stroke(
            width = strokePx,
            cap = StrokeCap.Round
        )
    )
}


private fun customGlowPain(glowColor: Color, glowPx: Float): Paint {
    require(glowPx > 0f) { "Glow px < 0f: $glowPx" }
    return Paint().apply {
        this.color = glowColor.copy(alpha = 0.7f).toArgb()
        style = Paint.Style.STROKE
        strokeWidth = glowPx
        maskFilter = android.graphics.BlurMaskFilter(
            glowPx,
            android.graphics.BlurMaskFilter.Blur.NORMAL
        )
        isAntiAlias = true
    }
}
