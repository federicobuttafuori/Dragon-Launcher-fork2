package org.elnix.dragonlauncher.ui.remembers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.delay
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.circles.LiveNestHitResult
import org.elnix.dragonlauncher.common.utils.circles.resolveLiveNestHit
import org.elnix.dragonlauncher.common.utils.circles.scaleDragDistances
import org.elnix.dragonlauncher.common.utils.circles.uiCirclesFromScaledDragDistances
import org.elnix.dragonlauncher.common.utils.UiCircle

/*  ─────────────  Live Nest public state  ─────────────  */

/**
 * Snapshot of Live Nest state returned per recomposition.
 *
 * @property isActive True while the user is inside an active Live Nest overlay.
 * @property hostPoint The parent point whose hold triggered the Live Nest.
 * @property nestedNest The [CircleNest] being rendered as a scaled overlay.
 * @property liveNestScale Scale applied to ring radii (0.3–1.0).
 * @property liveNestCenter Finger position at the moment Live Nest activated; used as the
 *   drawing center and hit-test origin so the overlay appears around the host point
 *   rather than at the gesture-start origin.
 * @property scaledUiCircles Pre-computed scaled ring list, ready for drawing.
 * @property nestedHit Real-time hit result while [isActive]; null otherwise.
 * @property suppressMainLaunch True after an abort — blocks main-nest action on release.
 */
data class LiveNestState(
    val isActive: Boolean,
    val hostPoint: SwipePointSerializable?,
    val nestedNest: CircleNest?,
    val liveNestScale: Float,
    val liveNestCenter: Offset?,
    val scaledUiCircles: List<UiCircle>,
    val nestedHit: LiveNestHitResult?,
    val suppressMainLaunch: Boolean,
    /**
     * Resolve which action to fire on finger-up.
     * Returns:
     *  - the nested point (Case A)
     *  - the host point (Case B – cancel zone)
     *  - null (Cases C/F – aborted, or not active)
     */
    val resolveOnRelease: () -> SwipePointSerializable?,
    /** Call after a successful nested launch to clean up all Live Nest state. */
    val clearAfterLaunch: () -> Unit
)

/*  ─────────────  Controller composable  ─────────────  */

/**
 * Composable controller that manages the Live Nest hold timer, activation,
 * real-time nested hit-test, bounds-abort suppression, and release resolution.
 *
 * Designed to keep [MainScreenOverlay] thin: all Live Nest state logic lives here.
 *
 * @param currentAction Currently selected swipe point on the main nest (or null).
 * @param isDragging True while a finger is on screen.
 * @param start Gesture origin offset (same value as used in overlay).
 * @param current Current finger position.
 * @param nests All available [CircleNest]s.
 * @param points All swipe points (filtered internally to the nested nest).
 * @param snapToOuterCircle Same flag as [BehaviorSettingsStore.pointsActionSnapsToOuterCircle].
 */
@Composable
fun rememberLiveNestController(
    currentAction: SwipePointSerializable?,
    isDragging: Boolean,
    start: Offset?,
    current: Offset?,
    nests: List<CircleNest>,
    points: List<SwipePointSerializable>,
    snapToOuterCircle: Boolean
): LiveNestState {

    /*  ─────────────  Mutable state  ─────────────  */

    var liveNestActive by remember { mutableStateOf(false) }
    var hostPoint by remember { mutableStateOf<SwipePointSerializable?>(null) }
    var nestedNest by remember { mutableStateOf<CircleNest?>(null) }
    var liveNestScale by remember { mutableStateOf(0.5f) }
    // Center for both drawing and hit-test: snapped to the finger position at activation time
    // so the overlay appears around the host point, not at the gesture-start origin.
    var liveNestCenter by remember { mutableStateOf<Offset?>(null) }
    var suppressMainLaunch by remember { mutableStateOf(false) }

    // Bumped each time an in-gesture abort happens; forces the hold timer to restart (Case F).
    var timerResetBump by remember { mutableIntStateOf(0) }

    // Plain ref tracking current finger position across frames so the timer coroutine
    // can read the up-to-date position at the moment activation fires.
    val currentRef = remember { object { var value: Offset? = null } }

    // Non-State ref used to snapshot the last hit before start/current go null on finger-up.
    val releaseHitRef = remember { object { var value: LiveNestHitResult? = null } }

    /*  ─────────────  Real-time hit-test  ─────────────  */

    // Use liveNestCenter (activation-time snapshot) as origin, not start.
    // This ensures the finger starts at center (dist≈0) instead of at the main-ring distance,
    // preventing an immediate out-of-bounds abort right after activation.
    val liveNestHit: LiveNestHitResult? = remember(liveNestActive, liveNestCenter, current, nestedNest, liveNestScale, snapToOuterCircle, hostPoint) {
        if (!liveNestActive || liveNestCenter == null || current == null || nestedNest == null) {
            null
        } else {
            resolveLiveNestHit(
                center = liveNestCenter!!,
                pointerPos = current,
                nestedNest = nestedNest!!,
                liveNestScale = liveNestScale,
                points = points,
                snapToOuterCircle = snapToOuterCircle,
                graceDistancePx = (hostPoint?.liveNestGraceDistancePx ?: 0).toFloat()
            )
        }
    }

    // Keep refs up-to-date after every composition (SideEffect runs post-composition).
    SideEffect {
        currentRef.value = current
        if (liveNestHit != null) {
            releaseHitRef.value = liveNestHit
        }
    }

    /*  ─────────────  Scaled ring list for drawing  ─────────────  */

    val scaledUiCircles: List<UiCircle> = remember(nestedNest, liveNestScale) {
        val nest = nestedNest ?: return@remember emptyList()
        uiCirclesFromScaledDragDistances(scaleDragDistances(nest.dragDistances, liveNestScale))
    }

    /*  ─────────────  Suppress / new gesture reset  ─────────────  */

    LaunchedEffect(isDragging) {
        if (isDragging) {
            // New pointer-down gesture: clear any carry-over suppression from a previous abort.
            suppressMainLaunch = false
            timerResetBump = 0
        } else {
            // Finger lifted: clear the activation center (will be re-set on next activation).
            liveNestCenter = null
        }
    }

    /*  ─────────────  Hold timer (Cases D, E, F)  ─────────────  */

    LaunchedEffect(currentAction?.id, isDragging, timerResetBump) {
        if (!isDragging || currentAction == null) return@LaunchedEffect
        // Do not start a new timer while an existing Live Nest is already open.
        if (liveNestActive) return@LaunchedEffect

        val targetNestId = currentAction.liveNestTargetNestId ?: return@LaunchedEffect
        val delayMs = (currentAction.liveNestPreviewDelayMs ?: 500).toLong()
        val scale = currentAction.liveNestScale ?: 0.5f

        delay(delayMs)

        /*  ─── Activate Live Nest after full hold ───  */
        // Snapshot the finger position NOW (after the delay) so the overlay is centered
        // where the finger actually is, not where the gesture started.
        val center = currentRef.value ?: return@LaunchedEffect
        val nest = nests.firstOrNull { it.id == targetNestId } ?: CircleNest()
        hostPoint = currentAction
        nestedNest = nest
        liveNestScale = scale
        liveNestCenter = center
        liveNestActive = true
    }

    /*  ─────────────  Bounds abort (Cases C / F)  ─────────────  */

    LaunchedEffect(liveNestHit?.isOutsideBounds) {
        if (liveNestHit?.isOutsideBounds == true && liveNestActive) {
            liveNestActive = false
            hostPoint = null
            nestedNest = null
            liveNestCenter = null
            suppressMainLaunch = true
            timerResetBump++  // force timer restart so Case F works inside the same gesture
        }
    }

    /*  ─────────────  Release resolution helpers  ─────────────  */

    val capturedHost = hostPoint

    val resolveOnRelease: () -> SwipePointSerializable? = remember(liveNestActive, capturedHost, suppressMainLaunch) {
        {
            val lastHit = releaseHitRef.value
            when {
                liveNestActive -> {
                    // Finger lifted while Live Nest was still open
                    when {
                        lastHit == null || lastHit.isInCancelZone -> capturedHost  // Case B
                        lastHit.selectedPoint != null -> lastHit.selectedPoint       // Case A
                        else -> capturedHost                                         // Case B fallback
                    }
                }
                suppressMainLaunch -> null  // Cases C / F: abort, no action
                else -> null               // caller handles normal (non-live-nest) path
            }
        }
    }

    val clearAfterLaunch: () -> Unit = remember {
        {
            liveNestActive = false
            hostPoint = null
            nestedNest = null
            liveNestCenter = null
            suppressMainLaunch = false
            releaseHitRef.value = null
        }
    }

    return LiveNestState(
        isActive = liveNestActive,
        hostPoint = hostPoint,
        nestedNest = nestedNest,
        liveNestScale = liveNestScale,
        liveNestCenter = liveNestCenter,
        scaledUiCircles = scaledUiCircles,
        nestedHit = liveNestHit,
        suppressMainLaunch = suppressMainLaunch,
        resolveOnRelease = resolveOnRelease,
        clearAfterLaunch = clearAfterLaunch
    )
}
