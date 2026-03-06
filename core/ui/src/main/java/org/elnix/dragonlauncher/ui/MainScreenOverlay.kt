@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.elnix.dragonlauncher.base.theme.LocalExtraColors
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.logging.logI
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.CustomObjectSerializable
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.Constants.Logging.ANGLE_LINE_TAG
import org.elnix.dragonlauncher.common.utils.Constants.Logging.NESTS_TAG
import org.elnix.dragonlauncher.common.utils.UiCircle
import org.elnix.dragonlauncher.common.utils.UiConstants
import org.elnix.dragonlauncher.common.utils.circles.computePointPosition
import org.elnix.dragonlauncher.common.utils.vibrate
import org.elnix.dragonlauncher.settings.stores.AngleLineSettingsStore
import org.elnix.dragonlauncher.settings.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.settings.stores.DebugSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.components.AppPreviewTitle
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.helpers.customobjects.customObject
import org.elnix.dragonlauncher.ui.helpers.nests.actionsInCircle
import org.elnix.dragonlauncher.ui.helpers.nests.circlesSettingsOverlay
import org.elnix.dragonlauncher.ui.helpers.nests.drawNeonGlowArc
import org.elnix.dragonlauncher.ui.helpers.nests.drawNeonGlowLine
import org.elnix.dragonlauncher.ui.helpers.nests.swipeDefaultParams
import org.elnix.dragonlauncher.ui.remembers.LocalAngleLineObject
import org.elnix.dragonlauncher.ui.remembers.LocalEndLineObject
import org.elnix.dragonlauncher.ui.remembers.LocalLineObject
import org.elnix.dragonlauncher.ui.remembers.LocalNests
import org.elnix.dragonlauncher.ui.remembers.LocalPoints
import org.elnix.dragonlauncher.ui.remembers.LocalStartLineObject
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot


@Composable
fun MainScreenOverlay(
    start: Offset?,
    current: Offset?,
    nestId: Int,
    onLaunch: ((SwipePointSerializable) -> Unit)?
) {
    val ctx = LocalContext.current
    val nests = LocalNests.current
    val points = LocalPoints.current
    val extraColors = LocalExtraColors.current

    val lineObject = LocalLineObject.current
    val angleLineObject = LocalAngleLineObject.current
    val startObject = LocalStartLineObject.current
    val endObject = LocalEndLineObject.current

    val rgbLine by UiSettingsStore.rgbLine.asState()
    val debugInfos by DebugSettingsStore.debugInfos.asState()

    val showLaunchingAppLabel by UiSettingsStore.showLaunchingAppLabel.asState()
    val showLaunchingAppIcon by UiSettingsStore.showLaunchingAppIcon.asState()

    val showAppLaunchPreview by UiSettingsStore.showAppLaunchingPreview.asState()
    val showAppCirclePreview by UiSettingsStore.showCirclePreview.asState()

    val showLineObjectPreview by AngleLineSettingsStore.showLineObjectPreview.asState()
    val showAngleLineObjectPreview by AngleLineSettingsStore.showAngleLineObjectPreview.asState()
    val showStartObjectPreview by AngleLineSettingsStore.showLineObjectPreview.asState()
    val showEndObjectPreview by AngleLineSettingsStore.showAngleLineObjectPreview.asState()

    val showAppPreviewIconCenterStartPosition by UiSettingsStore.showAppPreviewIconCenterStartPosition.asState()
    val linePreviewSnapToAction by UiSettingsStore.linePreviewSnapToAction.asState()
    val showAllActionsOnCurrentCircle by UiSettingsStore.showAllActionsOnCurrentCircle.asState()
    val showAllActionsOnCurrentNest by UiSettingsStore.showAllActionsOnCurrentNest.asState()
    val appLabelIconOverlayTopPadding by UiSettingsStore.appLabelIconOverlayTopPadding.asState()
    val appLabelOverlaySize by UiSettingsStore.appLabelOverlaySize.asState()
    val appIconOverlaySize by UiSettingsStore.appIconOverlaySize.asState()
    val disableHapticFeedback by BehaviorSettingsStore.disableHapticFeedbackGlobally.asState()
    val pointsActionSnapsToOuterCircle by BehaviorSettingsStore.pointsActionSnapsToOuterCircle.asState()


    val isDragging = start != null && current != null

    val currentNest = nests.find { it.id == nestId } ?: CircleNest()

    var lastAngle by remember { mutableStateOf<Double?>(null) }
    var cumulativeAngle by remember { mutableDoubleStateOf(0.0) }   // continuous rotation without jumps


    val dragRadii = nests.find { it.id == nestId }?.dragDistances ?: CircleNest().dragDistances
    val haptics = nests.find { it.id == nestId }?.haptic ?: CircleNest().haptic
    val minAngles =
        nests.find { it.id == nestId }?.minAngleActivation ?: CircleNest().minAngleActivation

    val dx: Float
    val dy: Float
    val dist: Float
    val angleRad: Double
    val angleDeg: Double
    val angle0to360: Double

    val lineColor: Color

    if (isDragging) {
        dx = current.x - start.x
        dy = current.y - start.y
        dist = hypot(dx, dy)

        // angle relative to UP = 0°
        angleRad = atan2(dx.toDouble(), -dy.toDouble())
        angleDeg = Math.toDegrees(angleRad)
        angle0to360 = if (angleDeg < 0) angleDeg + 360 else angleDeg

        // --- smooth 360° tracking ---
        lastAngle?.let { prev ->
            val diff = angle0to360 - prev

            val adjustedDiff = when {
                diff > 180 -> diff - 360   // jumped CW past 360→0
                diff < -180 -> diff + 360   // jumped CCW past 0→360
                else -> diff                // normal small movement
            }

            cumulativeAngle += adjustedDiff
        }
        @Suppress("AssignedValueIsNeverRead")
        lastAngle = angle0to360


        lineColor = if (rgbLine) Color.hsv(angle0to360.toFloat(), 1f, 1f)
        else extraColors.angleLine

    } else {
        dx = 0f; dy = 0f
        dist = 0f
        angleDeg = 0.0
        angle0to360 = 0.0
        cumulativeAngle = 0.0
        lineColor = Color.Transparent
    }

    val sweepAngle = (cumulativeAngle % 360).toFloat()

    var exposedClosest by remember { mutableStateOf<SwipePointSerializable?>(null) }
    var exposedAsbAngle by remember { mutableStateOf<Double?>(null) }


    // ───────────── For displaying the banner ─────────────
    var hoveredPoint by remember { mutableStateOf<SwipePointSerializable?>(null) }


    // The chosen swipe action
    var currentAction: SwipePointSerializable? by remember { mutableStateOf(null) }


    // Computes the target circle based on the mode selected
    val targetCircle = if (pointsActionSnapsToOuterCircle) {
        var best: Map.Entry<Int, Int>? = null

        for (entry in dragRadii) {
            if (dist <= entry.value) {
                if (best == null || entry.value < best.value) {
                    best = entry
                }
            }
        }

        best?.key ?: dragRadii.maxByOrNull { it.value }!!.key
    } else {
        var best: Map.Entry<Int, Int>? = null

        for (entry in dragRadii) {
            if (dist >= entry.value) {
                if (best == null || entry.value > best.value) {
                    best = entry
                }
            }
        }

        best?.key ?: dragRadii.minByOrNull { it.value }!!.key
    }



    if (isDragging) {

        val closestPoint =
            points.filter { it.nestId == nestId && it.circleNumber == targetCircle }
                .minByOrNull {
                    val d = abs(it.angleDeg - angle0to360)
                    minOf(d, 360 - d)
                }

        exposedClosest = closestPoint

        val selectedPoint = closestPoint?.let { p ->
            val d = abs(p.angleDeg - angle0to360)
            val shortest = minOf(d, 360 - d)
            exposedAsbAngle = shortest

            // if not provided, uses infinite angle, aka no limit
            val minAngleTargetCircle = minAngles[targetCircle] ?: 0

            // If minAngle == 0 -> no limit, always accept closest
            if (minAngleTargetCircle == 0 ||
                shortest <= minAngleTargetCircle
            ) {
                p
            } else {
                null
            }
        }

        currentAction = selectedPoint

        hoveredPoint = currentAction
    } else {
        exposedClosest = null
    }


    LaunchedEffect(hoveredPoint?.id) {
        hoveredPoint?.let { point ->
            (point.haptic ?: haptics[targetCircle]
            ?: defaultHapticFeedback(targetCircle)).let { milliseconds ->
                if (milliseconds > 0 && !disableHapticFeedback) {
                    vibrate(ctx, milliseconds.toLong())
                }
            }
        }
    }

    LaunchedEffect(isDragging) {
        if (!isDragging) {
            if (currentAction != null) {
                onLaunch?.invoke(currentAction!!)
            }
            hoveredPoint = null
            currentAction = null
        }
    }


    val circles: SnapshotStateList<UiCircle> = remember { mutableStateListOf() }

    LaunchedEffect(nestId) {
        // Clear previous circles before recomputing
        circles.clear()

        // Iterate over all circle numbers, excluding the special -1 key
        currentNest.dragDistances
            .filter { it.key != -1 }
            .forEach { (circleNumber, radius) ->

                // Add a new UiCircle with computed radius
                circles.add(
                    UiCircle(
                        id = circleNumber,
                        radius = radius.toFloat(),
                    )
                )
            }
    }

    val filteredCircles = circles.filter {
        showAllActionsOnCurrentNest ||
                (showAllActionsOnCurrentCircle && it.id == targetCircle)
    }


    Box(Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            if (debugInfos) {
                Text(
                    text = "start = ${start?.let { "%.1f, %.1f".format(it.x, it.y) } ?: "—"}",
                    color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium
                )
                Text(
                    text = "current = ${current?.let { "%.1f, %.1f".format(it.x, it.y) } ?: "—"}",
                    color = Color.White, fontSize = 12.sp)
                Text(
                    text = "dx = %.1f   dy = %.1f".format(dx, dy),
                    color = Color.White, fontSize = 12.sp
                )
                Text(
                    text = "dist = %.1f".format(dist),
                    color = Color.White, fontSize = 12.sp
                )
                Text(
                    text = "angle raw = %.1f°".format(angleDeg),
                    color = Color.White, fontSize = 12.sp
                )
                Text(
                    text = "angle 0–360 = %.1f°".format(angle0to360),
                    color = Color.White, fontSize = 12.sp
                )
                Text(
                    text = "closest point angle = ${exposedClosest?.angleDeg ?: "—"}",
                    color = Color.White, fontSize = 12.sp
                )
                Text(
                    text = "asb angle to closest point= $exposedAsbAngle",
                    color = Color.White, fontSize = 12.sp
                )
                Text(
                    text = "drag = $isDragging",
                    color = Color.White, fontSize = 12.sp
                )
                Text(
                    text = "target circle = $targetCircle",
                    color = Color.White, fontSize = 12.sp
                )
                Text(
                    text = "current action = $currentAction",
                    color = Color.White, fontSize = 12.sp
                )
            }
        }

//        val colorAction = if (hoveredPoint != null) actionColor(hoveredPoint!!.action, extraColors) else Color.Unspecified


//        val filteredPoints = points.filter {
//            it.nestId == nestId && (
//                when {
//                    showAllActionsOnCurrentNest -> true
//                    showAllActionsOnCurrentCircle -> it.circleNumber == targetCircle
//                    showAppLaunchPreview -> it.id == hoveredPoint?.id
//                    else -> false
//                }
//            )
//        }

        val drawParams = swipeDefaultParams(points = points)


        // Main drawing canva (the lines, circles and selected actions
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { // I use that to let the action in circle remove the background, otherwise it doesn't work
                    compositingStrategy = CompositingStrategy.Offscreen
                }
        ) {

            // Draw only if the user is dragging (has a start pos and a end (current) Offsets
            if (isDragging) {

                // If the line doesn't snap, I draw it here; first, when the user is dragging
                if (!linePreviewSnapToAction) {
                    actionLine(
                        start = start,
                        end = current,

                        showLineObjectPreview = showLineObjectPreview,
                        showAngleLineObjectPreview = showAngleLineObjectPreview,
                        showStartObjectPreview = showStartObjectPreview,
                        showEndObjectPreview = showEndObjectPreview,

                        lineCustomObject = lineObject,
                        angleLineCustomObject = angleLineObject,
                        startCustomObject = startObject,
                        endCustomObject = endObject,
                        sweepAngle = sweepAngle,

                        lineColor = lineColor
                    )
                }

                hoveredPoint?.let { point ->

                    // same circle radii as SettingsScreen
                    val radius = dragRadii[targetCircle]!!.toFloat()

                    // if you choose to draw every action, they are drawn here, excepted for
                    // the selected one, that is always drawn last to prevent overlapping issues,
                    // even though it shouldn't happen due to my separatePoints functions
//                    if (showAllActionsOnCurrentCircle) {
//                        points.filter { it.nestId == nestId && it.circleNumber == targetCircle && it != point }
//                            .forEach { p ->
//                                val localCenter = computePointPosition(
//                                    point = p,
//                                    radius = radius,
//                                    center = start
//                                )
//                                actionsInCircle(
//                                    selected = false,
//                                    point = p,
//                                    drawParams = drawParams,
//                                    center = localCenter,
//                                    depth = 1
//                                )
//                            }
//                    }


                    // compute point position relative to origin
                    // Depends on whether the line snaps or not to the closest point
                    val end = computePointPosition(
                        point = point,
                        radius = radius,
                        center = start
                    )

                    // If the line snaps, I draw it here once
                    if (linePreviewSnapToAction) {
                        actionLine(
                            start = start,
                            end = end,

                            showLineObjectPreview = showLineObjectPreview,
                            showAngleLineObjectPreview = showAngleLineObjectPreview,
                            showStartObjectPreview = showStartObjectPreview,
                            showEndObjectPreview = showEndObjectPreview,

                            lineCustomObject = lineObject,
                            angleLineCustomObject = angleLineObject,
                            startCustomObject = startObject,
                            endCustomObject = endObject,
                            sweepAngle = sweepAngle,

                            lineColor = lineColor
                        )
                    }

                    drawIntoCanvas { canvas ->

                        val bounds = Rect(0f, 0f, size.width, size.height)

                        canvas.saveLayer(bounds, Paint())

                        if (showAllActionsOnCurrentCircle || showAllActionsOnCurrentNest) {
                            logI(NESTS_TAG, "Got circle settings\ncircles: $circles\nfiltered: $filteredCircles")
                            // If you selected to draw the selected circle / nest
                            circlesSettingsOverlay(
                                drawParams = drawParams,
                                center = start,
                                depth = 1,
                                circles = filteredCircles,
                                selectedPoint = point,
                                nestId = nestId
                            )
                        } else if (showAppLaunchPreview) {
                            logI(NESTS_TAG, "Got action in settings")


                            // Main circle (the selected) drawn before any apps to be behind
                            if (showAppCirclePreview) {
                                drawCircle(
                                    color = extraColors.circle,
                                    radius = radius,
                                    center = start,
                                    style = Stroke(4f)
                                )
                            }

                            // Only draw here the point both show
                            actionsInCircle(
                                selected = true,
                                point = point,
                                drawParams = drawParams,
                                center = end,
                                depth = 1
                            )
                        } else {
                            logI(NESTS_TAG, "Got else")
                        }


                        // Show the current selected app in the center of the circle (start pos)
                        if (showAppPreviewIconCenterStartPosition) {
                            actionsInCircle(
                                selected = true,
                                point = point,
                                drawParams = drawParams,
                                center = start,
                                depth = 1
                            )
                        }

                        canvas.restore()
                    }
                }
            }
        }
    }


    // Label on top of the screen to indicate the launching app
    if (showLaunchingAppLabel || showLaunchingAppIcon) {
        AppPreviewTitle(
            point = hoveredPoint,
            topPadding = appLabelIconOverlayTopPadding.dp,
            labelSize = appLabelOverlaySize,
            iconSize = appIconOverlaySize,
            showLabel = showLaunchingAppLabel,
            showIcon = showLaunchingAppIcon
        )
    }
}


fun DrawScope.actionLine(
    start: Offset,
    end: Offset,

    showLineObjectPreview: Boolean,
    showAngleLineObjectPreview: Boolean,
    showStartObjectPreview: Boolean,
    showEndObjectPreview: Boolean,

    lineCustomObject: CustomObjectSerializable,
    angleLineCustomObject: CustomObjectSerializable,
    startCustomObject: CustomObjectSerializable,
    endCustomObject: CustomObjectSerializable,
    sweepAngle: Float,
    lineColor: Color
) {
    logD(ANGLE_LINE_TAG, lineCustomObject.toString())
    if (showLineObjectPreview) {
        val lineGlow = lineCustomObject.glow
        val lineStrokeWidth = (lineCustomObject.stroke ?: UiConstants.defaultLineCustomObject.stroke!!).dp.toPx()

        if (lineGlow != null) {
            val glowRadius = (lineGlow.radius ?: UiConstants.defaultLineCustomObject.glow!!.radius!!).dp.toPx()
            if (glowRadius > 0f) {
                drawNeonGlowLine(
                    start = start,
                    end = end,
                    color = lineCustomObject.color ?: lineColor,
                    lineStrokeWidth = lineStrokeWidth,
                    glowRadius = glowRadius,
                    glowColor = lineGlow.color ?: lineColor,
                    erase = lineCustomObject.eraseBackground ?: UiConstants.defaultLineCustomObject.eraseBackground!!
                )
            }
        } else if (lineStrokeWidth > 0f) {
            // Draw the main line from `start` to `end`
            drawLine(
                color = lineColor,
                start = start,
                end = end,
                strokeWidth = lineStrokeWidth,
                cap = StrokeCap.Round
            )
        }
    }

    if (showStartObjectPreview) {
        customObject(
            customObject = startCustomObject,
            default = UiConstants.defaultStartCustomObject,
            angleColor = lineColor,
            center = start
        )
    }


    if (showEndObjectPreview) {
        customObject(
            customObject = endCustomObject,
            default = UiConstants.defaultEndCustomObject,
            angleColor = lineColor,
            center = end
        )
    }


    // The angle rotating around the start point (have to fix that and allow more customization) TODO
    // The "do you hate it?" thing in settings
    if (showAngleLineObjectPreview) {

        val arcStroke = (angleLineCustomObject.stroke ?: UiConstants.defaultAngleCustomObject.stroke!!).dp.toPx()

        if (arcStroke > 0f) {
            val arcRadius =
                (angleLineCustomObject.size ?: UiConstants.defaultAngleCustomObject.size!!)
                    .dp.toPx()

            val rect = Rect(
                start.x - arcRadius,
                start.y - arcRadius,
                start.x + arcRadius,
                start.y + arcRadius
            )

            val angleGLow = angleLineCustomObject.glow


            val glowRadius = if (angleGLow != null ) {
                angleGLow.radius
                    ?: UiConstants.defaultAngleCustomObject.glow!!.radius!!
            } else 0f

            val glowColor = if (angleGLow != null ) {
                angleGLow.color
                    ?: UiConstants.defaultAngleCustomObject.glow!!.color
            } else null


            drawNeonGlowArc(
                topLeft = rect.topLeft,
                size = Size(rect.width, rect.height),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                color = angleLineCustomObject.color ?: lineColor,
                lineStrokeWidth = arcStroke,
                glowRadius = glowRadius,
                glowColor = glowColor,
                erase = angleLineCustomObject.eraseBackground
                    ?: UiConstants.defaultLineCustomObject.eraseBackground!!
            )
        }
    }
}


fun defaultHapticFeedback(id: Int): Int = when (id) {
    -1 -> 5 // Cancel Zone, small feedback
    0 -> 20  // First circle 20ms
    else -> 20 + 20 * id // others: add 20ms each
}
