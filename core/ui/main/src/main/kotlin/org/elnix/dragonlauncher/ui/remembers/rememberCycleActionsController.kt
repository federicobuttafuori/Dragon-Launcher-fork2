package org.elnix.dragonlauncher.ui.remembers

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.elnix.dragonlauncher.common.serializables.CycleActionStage
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.performCustomHaptic
import org.elnix.dragonlauncher.ui.defaultHapticFeedback

/*  ─────────────  Cycle Actions public state  ─────────────  */

/**
 * Snapshot of Cycle Actions state returned per recomposition.
 *
 * @property isActive True while the finger is down on a point with Cycle Actions configured.
 * @property currentStageIndex 0 = base action (point's own action); 1..N = extra timed stage.
 * @property currentStageAction Action for the current extra stage. Null when on the base stage.
 * @property resolveOnRelease Returns the extra-stage action to fire on release, or null when the
 *   base stage is current (caller should fire the point's own action in that case).
 * @property clear Resets all cycle state; call after a launch or after a cancel.
 */
data class CycleActionsState(
    val isActive: Boolean,
    val currentStageIndex: Int,
    val currentStageAction: SwipeActionSerializable?,
    val resolveOnRelease: () -> SwipeActionSerializable?,
    val clear: () -> Unit
)

/*  ─────────────  Controller composable  ─────────────  */

/**
 * Composable controller that manages the Cycle Actions elapsed timer, stage derivation,
 * per-stage haptic pulses, and release resolution.
 *
 * Runs independently of [rememberLiveNestController]. The overlay passes null for [currentAction]
 * when Live Nest is active, which automatically pauses the timer and deactivates cycle state.
 *
 * @param currentAction Currently selected point on the main nest, or null if Live Nest is active.
 * @param isDragging    True while a finger is on screen.
 * @param ctx           Android context used for haptic vibration calls.
 * @param disableHapticFeedback When true, no haptic pulses are played on stage transitions.
 */
@Composable
fun rememberCycleActionsController(
    currentAction: SwipePointSerializable?,
    isDragging: Boolean,
    ctx: Context,
    disableHapticFeedback: Boolean
): CycleActionsState {

    val stages: List<CycleActionStage>? = currentAction?.cycleActions

    /*  ─────────────  Mutable state  ─────────────  */

    // Kept as a stable MutableIntState object so the release/clear lambdas always read
    // the live value without needing to be recreated on every stage tick.
    val stageIndexState = remember { mutableIntStateOf(0) }
    var currentStageIndex by stageIndexState

    /*  ─────────────  Elapsed timer and stage derivation  ─────────────  */

    LaunchedEffect(currentAction?.id, isDragging) {
        // When isDragging goes false, do NOT reset here: let the overlay's release guard read
        // the final stage index via resolveOnRelease() and then call clear() to reset.
        if (!isDragging) return@LaunchedEffect

        // Finger is down (new gesture, or hovered point changed): always start fresh.
        currentStageIndex = 0
        if (stages.isNullOrEmpty()) return@LaunchedEffect

        var elapsedMs = 0L
        var lastFiredStageIndex = 0

        while (isActive) {
            delay(16L)
            elapsedMs += 16L

            // Highest stage whose triggerTimeMs has been crossed.
            // indexOfLast returns -1 when none apply → newIndex stays 0 (base stage).
            val crossedIndex = stages.indexOfLast { elapsedMs >= it.triggerTimeMs }
            val newIndex = crossedIndex + 1  // 0 = base; 1..N = extra stage index

            if (newIndex != currentStageIndex) {
                currentStageIndex = newIndex

                // One haptic pulse per upward transition.
                if (!disableHapticFeedback && newIndex > lastFiredStageIndex) {
                    val haptic = stages[newIndex - 1].hapticFeedback
                        ?: defaultHapticFeedback(newIndex)
                    performCustomHaptic(ctx, haptic)
                    lastFiredStageIndex = newIndex
                }
            }

            // All thresholds crossed — stay on the last stage, stop ticking.
            if (newIndex >= stages.size) break
        }
    }

    /*  ─────────────  Release resolution helpers  ─────────────  */

    // Lambdas capture `stageIndexState` (the object), not a snapshot of the Int, so they
    // always return the most recent stage index at the time they are invoked.
    val resolveOnRelease: () -> SwipeActionSerializable? = remember(stages) {
        {
            val idx = stageIndexState.intValue
            if (idx == 0 || stages.isNullOrEmpty()) null else stages[idx - 1].action
        }
    }

    val clear: () -> Unit = remember { { stageIndexState.intValue = 0 } }

    return CycleActionsState(
        isActive = isDragging && !stages.isNullOrEmpty(),
        currentStageIndex = currentStageIndex,
        currentStageAction = if (currentStageIndex > 0 && !stages.isNullOrEmpty())
            stages[currentStageIndex - 1].action else null,
        resolveOnRelease = resolveOnRelease,
        clear = clear
    )
}
