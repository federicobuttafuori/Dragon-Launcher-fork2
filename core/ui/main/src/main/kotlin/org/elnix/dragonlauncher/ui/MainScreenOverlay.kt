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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.elnix.dragonlauncher.base.theme.LocalExtraColors
import org.elnix.dragonlauncher.logging.logI
import org.elnix.dragonlauncher.logging.logV
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.CustomHapticFeedbackSerializable
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.Constants.Logging.NESTS_TAG
import org.elnix.dragonlauncher.common.utils.UiCircle
import org.elnix.dragonlauncher.common.utils.circles.computePointPosition
import org.elnix.dragonlauncher.common.utils.performCustomHaptic
import org.elnix.dragonlauncher.common.utils.resolveShape
import org.elnix.dragonlauncher.settings.stores.AngleLineSettingsStore
import org.elnix.dragonlauncher.settings.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.settings.stores.DebugSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.base.UiConstants
import org.elnix.dragonlauncher.ui.components.AppPreviewTitle
import org.elnix.dragonlauncher.ui.base.asState
import org.elnix.dragonlauncher.ui.dialogs.rememberLineObjectsOrder
import org.elnix.dragonlauncher.ui.helpers.customobjects.actionLine
import org.elnix.dragonlauncher.ui.helpers.nests.actionsInCircle
import org.elnix.dragonlauncher.ui.helpers.nests.circlesSettingsOverlay
import org.elnix.dragonlauncher.ui.remembers.rememberSwipeDefaultParams
import org.elnix.dragonlauncher.ui.composition.LocalAngleLineObject
import org.elnix.dragonlauncher.ui.composition.LocalEndLineObject
import org.elnix.dragonlauncher.ui.composition.LocalLineObject
import org.elnix.dragonlauncher.ui.composition.LocalNests
import org.elnix.dragonlauncher.ui.composition.LocalPoints
import org.elnix.dragonlauncher.ui.composition.LocalStartLineObject
import org.elnix.dragonlauncher.ui.remembers.rememberSweepAngle
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot


// Data class to hold geometric calculations
data class DragData(
    val dist: Float,
    val angle0to360: Float,
    val angleDeg: Float
)


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
    val showStartObjectPreview by AngleLineSettingsStore.showStartObjectPreview.asState()
    val showEndObjectPreview by AngleLineSettingsStore.showEndObjectPreview.asState()

    val order by rememberLineObjectsOrder()


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

    val sweepState = rememberSweepAngle()


    // Optimization: Calculate geometric values only when dragging and using derivedStateOf
    // to avoid recomposing the entire overlay on every pixel move if the end result doesn't change.
    val dragData by remember(start, current) {
        derivedStateOf {
            if (isDragging) {
                val dxVal = current.x - start.x
                val dyVal = current.y - start.y
                val distVal = hypot(dxVal, dyVal)

                val angleRadVal = atan2(dxVal.toDouble(), -dyVal.toDouble())
                val angleDegVal = Math.toDegrees(angleRadVal)

                sweepState.onAngleChanged(angleDegVal.toFloat())

                DragData(distVal, sweepState.angle360(), sweepState.sweepAngle())
            } else {
                sweepState.reset()
                DragData(0f, 0f, 0f)
            }
        }
    }

    val dist = dragData.dist
    val angle360 = dragData.angle0to360
    val sweepAngle = dragData.angleDeg




    val currentNest = remember(nests, nestId) { nests.find { it.id == nestId } ?: CircleNest() }

    val dragRadii = currentNest.dragDistances
    val haptics = currentNest.haptic
    val minAngles = currentNest.minAngleActivation

    val lineColor: Color = if (isDragging) {
        if (rgbLine) Color.hsv(angle360, 1f, 1f)
        else extraColors.angleLine
    } else {
        Color.Transparent
    }

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
                    val d = abs(it.angleDeg - angle360)
                    minOf(d, 360 - d)
                }

        exposedClosest = closestPoint

        val selectedPoint = closestPoint?.let { p ->
            val d = abs(p.angleDeg - angle360)
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
            if (!disableHapticFeedback) {
                (point.hapticFeedback ?: haptics[targetCircle]
                ?: defaultHapticFeedback(targetCircle)).let { customHaptic ->
                    performCustomHaptic(ctx, customHaptic)
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

    val pickedRememberShapeAngle = remember(isDragging) {
        (angleLineObject.shape ?: UiConstants.defaultAngleCustomObject.shape).resolveShape()
    }
    val pickedRememberRotationAngle = remember(isDragging) {
        angleLineObject.rotation
            ?.takeIf { it != -1 }
            ?: (0..360).random()
    }

    val pickedRememberShapeStart = remember(isDragging) {
        (startObject.shape ?: UiConstants.defaultStartCustomObject.shape).resolveShape()
    }
    val pickedRememberRotationStart = remember(isDragging) {
        startObject.rotation
            ?.takeIf { it != -1 }
            ?: (0..360).random()
    }

    val pickedRememberShapeEnd = remember(isDragging) {
        (endObject.shape ?: UiConstants.defaultEndCustomObject.shape).resolveShape()
    }
    val pickedRememberRotationEnd = remember(isDragging) {
        endObject.rotation
            ?.takeIf { it != -1 }
            ?: (0..360).random()
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
                    text = "dist = %.1f".format(dist),
                    color = Color.White, fontSize = 12.sp
                )
                Text(
                    text = "sweep raw = %.1f°".format(sweepAngle),
                    color = Color.White, fontSize = 12.sp
                )
                Text(
                    text = "angle 0–360 = %.1f°".format(angle360),
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

        val drawParams = rememberSwipeDefaultParams(points = points)


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
                        sweepAngle = sweepAngle,
                        lineColor = lineColor,
                        order = order,
                        showLineObjectPreview = showLineObjectPreview,
                        showAngleLineObjectPreview = showAngleLineObjectPreview,
                        showStartObjectPreview = showStartObjectPreview,
                        showEndObjectPreview = showEndObjectPreview,
                        pickedRememberShapeAngle = pickedRememberShapeAngle,
                        pickedRememberRotationAngle = pickedRememberRotationAngle,
                        pickedRememberRotationStart = pickedRememberRotationStart,
                        pickedRememberShapeStart = pickedRememberShapeStart,
                        pickedRememberRotationEnd = pickedRememberRotationEnd,
                        pickedRememberShapeEnd = pickedRememberShapeEnd,
                        lineCustomObject = lineObject,
                        angleLineCustomObject = angleLineObject,
                        startCustomObject = startObject,
                        endCustomObject = endObject


                    )
                }

                hoveredPoint?.let { point ->

                    // same circle radii as SettingsScreen
                    val radius = dragRadii[targetCircle]!!.toFloat()

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
                            sweepAngle = sweepAngle,
                            lineColor = lineColor,
                            order = order,
                            showLineObjectPreview = showLineObjectPreview,
                            showAngleLineObjectPreview = showAngleLineObjectPreview,
                            showStartObjectPreview = showStartObjectPreview,
                            showEndObjectPreview = showEndObjectPreview,
                            pickedRememberShapeAngle = pickedRememberShapeAngle,
                            pickedRememberRotationAngle = pickedRememberRotationAngle,
                            pickedRememberRotationStart = pickedRememberRotationStart,
                            pickedRememberShapeStart = pickedRememberShapeStart,
                            pickedRememberRotationEnd = pickedRememberRotationEnd,
                            pickedRememberShapeEnd = pickedRememberShapeEnd,
                            lineCustomObject = lineObject,
                            angleLineCustomObject = angleLineObject,
                            startCustomObject = startObject,
                            endCustomObject = endObject
                        )
                    }

                    drawIntoCanvas { canvas ->

                        val bounds = Rect(0f, 0f, size.width, size.height)

                        canvas.saveLayer(bounds, Paint())

                        if (showAllActionsOnCurrentCircle || showAllActionsOnCurrentNest) {
                            logV(NESTS_TAG) { "Got circle settings\ncircles: $circles\nfiltered: $filteredCircles" }
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
                            logV(NESTS_TAG) { "Got action in settings" }


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
                            logI(NESTS_TAG) { "Got else" }
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


fun defaultHapticFeedback(id: Int): CustomHapticFeedbackSerializable = CustomHapticFeedbackSerializable(
    listOf(
        true to
                when (id) {
                    -1 -> 5 // Cancel Zone, small feedback
                    0 -> 20  // First circle 20ms
                    else -> 20 + 20 * id // others: add 20ms each
                }
    )
)
