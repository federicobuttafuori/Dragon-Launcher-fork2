

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
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.base.theme.LocalExtraColors
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.CustomHapticFeedbackSerializable
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
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
import org.elnix.dragonlauncher.ui.composition.LocalAppsViewModel
import org.elnix.dragonlauncher.ui.composition.LocalEndLineObject
import org.elnix.dragonlauncher.ui.composition.LocalLineObject
import org.elnix.dragonlauncher.ui.composition.LocalNests
import org.elnix.dragonlauncher.ui.composition.LocalPoints
import org.elnix.dragonlauncher.ui.composition.LocalStartLineObject
import org.elnix.dragonlauncher.ui.remembers.rememberCycleActionsController
import org.elnix.dragonlauncher.ui.remembers.rememberHoldAndRunController
import org.elnix.dragonlauncher.ui.remembers.rememberLiveNestController
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


    /*  ─────────────  Live Nest controller  ─────────────  */

    val liveNest = rememberLiveNestController(
        currentAction = currentAction,
        isDragging = isDragging,
        start = start,
        current = current,
        nests = nests,
        points = points,
        snapToOuterCircle = pointsActionSnapsToOuterCircle
    )

    // While Live Nest is active, null currentAction (prevents any main-nest action from firing)
    // but freeze hoveredPoint to the host point so the host circle and its app remain visible.
    if (liveNest.isActive) {
        currentAction = null
        hoveredPoint = liveNest.hostPoint
    }

    /*  ─────────────  Cycle Actions controller (main nest)  ─────────────  */

    // currentAction is null when Live Nest is active, which pauses the cycle timer automatically.
    val cycleActions = rememberCycleActionsController(
        currentAction = currentAction,
        isDragging = isDragging,
        ctx = ctx,
        disableHapticFeedback = disableHapticFeedback
    )

    // When a non-base cycle stage is active, substitute the stage's action in the preview point
    // so actionsInCircle and AppPreviewTitle reflect the action that will fire on release.
    // Loop Over reuses the last stage's action with a temporary label; customIcon is cleared
    // whenever either the base or staged action is OpenCircleNest (mini-nest rings need null icon).
    val loopOverLabel = stringResource(R.string.cycle_actions_loop_over)
    val displayPoint: SwipePointSerializable? = hoveredPoint?.let { hp ->
        val ca = hp.cycleActions
        if (ca.isNullOrEmpty()) return@let hp
        when {
            cycleActions.isLoopOverPhase -> {
                val last = ca.last().action
                if (last is SwipeActionSerializable.OpenCircleNest || hp.action is SwipeActionSerializable.OpenCircleNest)
                    hp.copy(action = last, customIcon = null, customName = loopOverLabel)
                else
                    hp.copy(action = last, customName = loopOverLabel)
            }
            cycleActions.currentStageIndex > 0 -> {
                val staged = ca.getOrNull(cycleActions.currentStageIndex - 1)?.action
                    ?: return@let hp
                if (staged is SwipeActionSerializable.OpenCircleNest || hp.action is SwipeActionSerializable.OpenCircleNest)
                    hp.copy(action = staged, customIcon = null)
                else
                    hp.copy(action = staged)
            }
            else -> hp
        }
    }

    val appsViewModel = LocalAppsViewModel.current

    /*  ─────────────  Hold & Run controller (main nest)  ─────────────  */

    // currentAction is null when Live Nest is active; Hold & Run timer won't start in that case.
    // If Hold & Run fires first (before Live Nest delay elapses) the gesture is consumed and the
    // release guard skips any further launch.
    var holdAndRunConsumed by remember { mutableStateOf(false) }
    val holdAndRun = rememberHoldAndRunController(
        currentAction = currentAction,
        isDragging = isDragging
    ) { firedPoint ->
        onLaunch?.invoke(firedPoint)
        holdAndRunConsumed = true
    }

    // Reset consumed flag at the start of each new gesture.
    LaunchedEffect(isDragging) {
        if (isDragging) holdAndRunConsumed = false
    }

    /*  ─────────────  Controllers for points inside the Live Nest  ─────────────  */

    // The currently selected point inside the active Live Nest — feeds the parallel controllers
    // below so that Cycle Actions, Hold & Run and inner Live Nests work identically to the
    // main nest.  Null when the outer Live Nest is not active.
    val liveNestedCurrentAction: SwipePointSerializable? =
        if (liveNest.isActive) liveNest.nestedHit?.selectedPoint else null

    val liveNestCycleActions = rememberCycleActionsController(
        currentAction = liveNestedCurrentAction,
        isDragging = isDragging,
        ctx = ctx,
        disableHapticFeedback = disableHapticFeedback
    )

    val liveNestHoldAndRun = rememberHoldAndRunController(
        currentAction = liveNestedCurrentAction,
        isDragging = isDragging
    ) { firedPoint ->
        onLaunch?.invoke(firedPoint)
    }

    // Inner Live Nest: a Live Nest triggered from a point that is itself inside the outer Live Nest.
    val innerLiveNest = rememberLiveNestController(
        currentAction = liveNestedCurrentAction,
        isDragging = isDragging,
        start = liveNest.liveNestCenter,
        current = current,
        nests = nests,
        points = points,
        snapToOuterCircle = pointsActionSnapsToOuterCircle
    )

    // Display point for the nested selection — applies the active Cycle Actions stage, if any.
    val liveNestDisplayPoint: SwipePointSerializable? = liveNestedCurrentAction?.let { np ->
        val ca = np.cycleActions
        if (ca.isNullOrEmpty()) return@let np
        when {
            liveNestCycleActions.isLoopOverPhase -> {
                val last = ca.last().action
                if (last is SwipeActionSerializable.OpenCircleNest || np.action is SwipeActionSerializable.OpenCircleNest)
                    np.copy(action = last, customIcon = null, customName = loopOverLabel)
                else
                    np.copy(action = last, customName = loopOverLabel)
            }
            liveNestCycleActions.currentStageIndex > 0 -> {
                val staged = ca.getOrNull(liveNestCycleActions.currentStageIndex - 1)?.action
                    ?: return@let np
                if (staged is SwipeActionSerializable.OpenCircleNest || np.action is SwipeActionSerializable.OpenCircleNest)
                    np.copy(action = staged, customIcon = null)
                else
                    np.copy(action = staged)
            }
            else -> np
        }
    }

    LaunchedEffect(
        hoveredPoint?.id,
        hoveredPoint?.cycleActions,
        liveNestedCurrentAction?.id,
        liveNestedCurrentAction?.cycleActions
    ) {
        fun preloadCycleIfNeeded(p: SwipePointSerializable?) {
            if (p == null) return
            if (p.cycleActions.isNullOrEmpty()) return
            val persisted = points.find { it.id == p.id } ?: p
            appsViewModel.preloadCycleLayerIcons(persisted)
        }
        preloadCycleIfNeeded(hoveredPoint)
        preloadCycleIfNeeded(liveNestedCurrentAction)
    }

    /*  Icon bitmaps are keyed by point id; [actionsInCircle] / [AppPreviewTitle] read
     *  icons[point.id]. Reload when the staged action changes so the cache matches
     *  [displayPoint.action], then restore from persisted [points] on release / drift.  */
    var lastHoveredSwipePointId by remember { mutableStateOf<String?>(null) }
    SideEffect {
        if (hoveredPoint != null) lastHoveredSwipePointId = hoveredPoint!!.id
    }

    LaunchedEffect(hoveredPoint?.id, cycleActions.currentStageIndex) {
        if (!isDragging) return@LaunchedEffect
        val hp = hoveredPoint ?: return@LaunchedEffect
        if (hp.cycleActions.isNullOrEmpty()) return@LaunchedEffect
        if (cycleActions.currentStageIndex == 0) return@LaunchedEffect
        val dp = displayPoint ?: return@LaunchedEffect
        appsViewModel.reloadPointIcon(dp)
    }

    LaunchedEffect(hoveredPoint?.id, isDragging) {
        if (isDragging && hoveredPoint == null) {
            lastHoveredSwipePointId?.let { id ->
                points.find { it.id == id }
                    ?.takeIf { !it.cycleActions.isNullOrEmpty() }
                    ?.let { appsViewModel.reloadPointIcon(it) }
            }
            lastHoveredSwipePointId = null
        }
    }

    // Icon reload for Cycle Actions inside the Live Nest.
    LaunchedEffect(liveNestedCurrentAction?.id, liveNestCycleActions.currentStageIndex) {
        if (!isDragging || !liveNest.isActive) return@LaunchedEffect
        val np = liveNestedCurrentAction ?: return@LaunchedEffect
        if (np.cycleActions.isNullOrEmpty()) return@LaunchedEffect
        if (liveNestCycleActions.currentStageIndex == 0) return@LaunchedEffect
        val dp = liveNestDisplayPoint ?: return@LaunchedEffect
        appsViewModel.reloadPointIcon(dp)
    }

    // Main-nest haptic — suppressed while Live Nest is active (interaction is frozen).
    LaunchedEffect(hoveredPoint?.id) {
        hoveredPoint?.let { point ->
            if (!disableHapticFeedback && !liveNest.isActive) {
                (point.hapticFeedback ?: haptics[targetCircle]
                ?: defaultHapticFeedback(targetCircle)).let { customHaptic ->
                    performCustomHaptic(ctx, customHaptic)
                }
            }
        }
    }

    // Haptic feedback when the selected nested point changes — same resolution hierarchy
    // as the main nest: point-level override → nest per-circle map → default by ring id.
    LaunchedEffect(liveNest.nestedHit?.selectedPoint?.id) {
        val hit = liveNest.nestedHit ?: return@LaunchedEffect
        val nestedPoint = hit.selectedPoint ?: return@LaunchedEffect
        if (!disableHapticFeedback) {
            val nestedTargetCircle = hit.targetCircle
            val nestedHaptics = liveNest.nestedNest?.haptic ?: emptyMap()
            val haptic = nestedPoint.hapticFeedback
                ?: nestedHaptics[nestedTargetCircle]
                ?: defaultHapticFeedback(nestedTargetCircle)
            performCustomHaptic(ctx, haptic)
        }
    }

    // Haptic for inner Live Nest activation.
    LaunchedEffect(innerLiveNest.nestedHit?.selectedPoint?.id) {
        val hit = innerLiveNest.nestedHit ?: return@LaunchedEffect
        val nestedPoint = hit.selectedPoint ?: return@LaunchedEffect
        if (!disableHapticFeedback) {
            val nestedTargetCircle = hit.targetCircle
            val nestedHaptics = innerLiveNest.nestedNest?.haptic ?: emptyMap()
            val haptic = nestedPoint.hapticFeedback
                ?: nestedHaptics[nestedTargetCircle]
                ?: defaultHapticFeedback(nestedTargetCircle)
            performCustomHaptic(ctx, haptic)
        }
    }

    // Haptic feedback on Live Nest activation — double-pulse to signal "overlay opened".
    LaunchedEffect(liveNest.isActive) {
        if (liveNest.isActive && !disableHapticFeedback) {
            performCustomHaptic(
                ctx,
                CustomHapticFeedbackSerializable(listOf(true to 30))
            )
        }
    }

    LaunchedEffect(innerLiveNest.isActive) {
        if (innerLiveNest.isActive && !disableHapticFeedback) {
            performCustomHaptic(
                ctx,
                CustomHapticFeedbackSerializable(listOf(true to 30))
            )
        }
    }

    /*  ─────────────  Release / launch guard  ─────────────  */

    LaunchedEffect(isDragging) {
        if (!isDragging) {
            // Always reset the icon cache to the persisted point (base action) when a gesture ends
            // on a Cycle Actions point. Do not gate on currentStageIndex: it can be out of sync with
            // the last bitmap we pushed into the cache via reloadPointIcon(displayPoint), and the
            // async reload must also use override so a race with preloadPointIcons cannot skip it.
            (hoveredPoint?.id ?: currentAction?.id)?.let { pid ->
                points.find { it.id == pid }
                    ?.takeIf { !it.cycleActions.isNullOrEmpty() }
                    ?.let { base ->
                        appsViewModel.preloadPointIcons(listOf(base), override = true)
                    }
            }
            when {
                liveNest.isActive -> {
                    when {
                        innerLiveNest.isActive -> {
                            // Inner Live Nest was open — resolve from it (Case A/B inside inner)
                            innerLiveNest.resolveOnRelease()?.let { onLaunch?.invoke(it) }
                            innerLiveNest.clearAfterLaunch()
                        }
                        liveNestHoldAndRun.firedThisGesture -> {
                            // Hold & Run already fired inside the Live Nest — nothing to do.
                        }
                        else -> {
                            // Normal Live Nest release — apply Cycle Actions stage if configured.
                            val nestedPoint = liveNest.resolveOnRelease()
                            if (nestedPoint != null) {
                                val stageAction = liveNestCycleActions.resolveOnRelease()
                                if (stageAction != null) {
                                    onLaunch?.invoke(nestedPoint.copy(action = stageAction))
                                } else {
                                    onLaunch?.invoke(nestedPoint)
                                }
                            }
                            liveNestCycleActions.clear()
                        }
                    }
                    liveNest.clearAfterLaunch()
                }
                liveNest.suppressMainLaunch -> {
                    // Abort happened this gesture (Cases C / F): fire nothing
                    // suppressMainLaunch will be cleared on the next pointer-down
                }
                holdAndRunConsumed -> {
                    // Hold & Run already fired during the hold — gesture is consumed; do nothing.
                    cycleActions.clear()
                    holdAndRunConsumed = false
                }
                else -> {
                    // Normal path — also handles Cycle Actions resolution when applicable.
                    // resolveOnRelease() returns the extra-stage action, or null for base stage.
                    currentAction?.let { point ->
                        val stageAction = cycleActions.resolveOnRelease()
                        if (stageAction != null) {
                            onLaunch?.invoke(point.copy(action = stageAction))
                        } else {
                            onLaunch?.invoke(point)
                        }
                        cycleActions.clear()
                    }
                }
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

    // When Live Nest is active, freeze the "current circle" reference to the host
    // point's ring so the main-nest overlay cannot react to raw finger movements
    // happening inside the Live Nest overlay.
    val effectiveTargetCircle =
        if (liveNest.isActive) (liveNest.hostPoint?.circleNumber ?: targetCircle) else targetCircle

    val filteredCircles = circles.filter {
        showAllActionsOnCurrentNest ||
                (showAllActionsOnCurrentCircle && it.id == effectiveTargetCircle)
    }

    val mainNestLayerAlpha = remember(liveNest.isActive, liveNest.hostPoint?.liveNestMainNestOpacityPercent) {
        val p = liveNest.hostPoint?.liveNestMainNestOpacityPercent
        if (liveNest.isActive && p != null) p.coerceIn(0, 100) / 100f else 1f
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

        /*  Main nest (lines + rings + icons) and Live Nest overlay are split so the host can
         *  dim the main layer via [SwipePointSerializable.liveNestMainNestOpacityPercent].  */
        Box(Modifier.fillMaxSize()) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = mainNestLayerAlpha
                        compositingStrategy = CompositingStrategy.Offscreen
                    }
            ) {

                if (isDragging) {

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

                    displayPoint?.let { point ->

                        val circleKey = if (liveNest.isActive) point.circleNumber else targetCircle
                        val radius = (dragRadii[circleKey] ?: dragRadii[targetCircle])!!.toFloat()

                        val end = computePointPosition(
                            point = point,
                            radius = radius,
                            center = start
                        )

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
                                circlesSettingsOverlay(
                                    drawParams = drawParams,
                                    center = start,
                                    depth = 1,
                                    circles = filteredCircles,
                                    selectedPoint = point,
                                    nestId = nestId
                                )
                            } else if (showAppLaunchPreview) {
                                if (showAppCirclePreview) {
                                    drawCircle(
                                        color = extraColors.circle,
                                        radius = radius,
                                        center = start,
                                        style = Stroke(4f)
                                    )
                                }

                                actionsInCircle(
                                    selected = true,
                                    point = point,
                                    drawParams = drawParams,
                                    center = end,
                                    depth = 1
                                )
                            }

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

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        compositingStrategy = CompositingStrategy.Offscreen
                    }
            ) {
                if (isDragging && liveNest.isActive) {
                    val nestedNestForDraw = liveNest.nestedNest
                    val liveNestCenterForDraw = liveNest.liveNestCenter
                    if (nestedNestForDraw != null && liveNestCenterForDraw != null) {
                        drawIntoCanvas { canvas ->
                            val bounds = Rect(0f, 0f, size.width, size.height)
                            canvas.saveLayer(bounds, Paint())

                            // When the inner Live Nest is active, freeze the outer overlay on its
                            // host (the point that triggered the inner nest) so the outer rings do
                            // not react to finger movements happening inside the inner overlay.
                            val outerSelectedPoint = if (innerLiveNest.isActive)
                                innerLiveNest.hostPoint
                            else
                                liveNestDisplayPoint ?: liveNest.nestedHit?.selectedPoint

                            circlesSettingsOverlay(
                                drawParams = drawParams,
                                center = liveNestCenterForDraw,
                                depth = 1,
                                circles = liveNest.scaledUiCircles,
                                selectedPoint = outerSelectedPoint,
                                nestId = nestedNestForDraw.id
                            )

                            canvas.restore()
                        }

                        // Inner Live Nest overlay — drawn on top of the outer nest.
                        val innerNestForDraw = innerLiveNest.nestedNest
                        val innerCenterForDraw = innerLiveNest.liveNestCenter
                        if (innerLiveNest.isActive && innerNestForDraw != null && innerCenterForDraw != null) {
                            drawIntoCanvas { canvas ->
                                val bounds = Rect(0f, 0f, size.width, size.height)
                                canvas.saveLayer(bounds, Paint())

                                circlesSettingsOverlay(
                                    drawParams = drawParams,
                                    center = innerCenterForDraw,
                                    depth = 1,
                                    circles = innerLiveNest.scaledUiCircles,
                                    selectedPoint = innerLiveNest.nestedHit?.selectedPoint,
                                    nestId = innerNestForDraw.id
                                )

                                canvas.restore()
                            }
                        }
                    }
                }
            }
        }
    }


    // Label on top of the screen.
    // Priority: inner Live Nest selection → outer Live Nest selection (with cycle stage) → main nest.
    if (showLaunchingAppLabel || showLaunchingAppIcon) {
        val labelPoint = when {
            innerLiveNest.isActive -> innerLiveNest.nestedHit?.selectedPoint
            liveNest.isActive      -> liveNestDisplayPoint
            else                   -> displayPoint
        }
        AppPreviewTitle(
            point = labelPoint,
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
