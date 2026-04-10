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
 * @property currentStageIndex 0 = base action; 1..N = timed stages; N+1 = "Loop Over" when loop is on.
 * @property currentStageAction Action for the current extra stage or Loop Over (last stage's action).
 *   Null when on the base stage.
 * @property isLoopOverPhase True while in the post-last-stage pause before the cycle restarts (loop on).
 * @property resolveOnRelease Returns the extra-stage action to fire on release, or null when the
 *   base stage is current (caller should fire the point's own action in that case).
 * @property clear Resets all cycle state; call after a launch or after a cancel.
 */
data class CycleActionsState(
    val isActive: Boolean,
    val currentStageIndex: Int,
    val currentStageAction: SwipeActionSerializable?,
    val isLoopOverPhase: Boolean,
    val resolveOnRelease: () -> SwipeActionSerializable?,
    val clear: () -> Unit
)

/*  ─────────────  Controller composable  ─────────────  */

/**
 * Composable controller that manages the Cycle Actions elapsed timer, stage derivation,
 * per-stage haptic pulses, optional loop wrap with a "Loop Over" tail, and release resolution.
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

    LaunchedEffect(
        currentAction?.id,
        isDragging,
        stages,
        currentAction?.cycleActionsLoopEnabled,
        currentAction?.cycleActionsLoopDelayMs
    ) {
        // When isDragging goes false, do NOT reset here: let the overlay's release guard read
        // the final stage index via resolveOnRelease() and then call clear() to reset.
        if (!isDragging) return@LaunchedEffect

        // Finger is down (new gesture, or hovered point changed): always start fresh.
        currentStageIndex = 0
        if (stages.isNullOrEmpty()) return@LaunchedEffect

        val act = currentAction!!
        val loopEnabled = act.cycleActionsLoopEnabled
        val loopDelayMs = (act.cycleActionsLoopDelayMs ?: 500).coerceAtLeast(1)
        val tLast = stages.last().triggerTimeMs.toLong()
        val cycleLen = tLast + loopDelayMs

        var elapsedMs = 0L
        var cycleBase = 0L
        var prevCycleBase = 0L
        var lastFiredStageIndex = 0

        while (isActive) {
            delay(16L)
            elapsedMs += 16L

            if (loopEnabled) {
                while (elapsedMs - cycleBase >= cycleLen) {
                    cycleBase += cycleLen
                }
                if (cycleBase != prevCycleBase) {
                    prevCycleBase = cycleBase
                    lastFiredStageIndex = -1
                }
            }

            val eff = elapsedMs - cycleBase

            val newIndex = if (loopEnabled && eff >= tLast && eff < tLast + loopDelayMs) {
                stages.size + 1
            } else {
                val crossedIndex = stages.indexOfLast { eff >= it.triggerTimeMs }
                (crossedIndex + 1).coerceAtMost(stages.size)
            }

            if (newIndex != currentStageIndex) {
                currentStageIndex = newIndex

                // One haptic pulse per upward transition (or when re-entering stages after a loop wrap).
                if (!disableHapticFeedback && newIndex > lastFiredStageIndex) {
                    val haptic = when {
                        newIndex == stages.size + 1 ->
                            defaultHapticFeedback(stages.size + 1)
                        newIndex in 1..stages.size ->
                            stages[newIndex - 1].hapticFeedback
                                ?: defaultHapticFeedback(newIndex)
                        else -> null
                    }
                    haptic?.let { performCustomHaptic(ctx, it) }
                    lastFiredStageIndex = newIndex
                }
            }

            if (!loopEnabled && newIndex >= stages.size) break
        }
    }

    /*  ─────────────  Release resolution helpers  ─────────────  */

    val loopEnabledForRelease = currentAction?.cycleActionsLoopEnabled == true

    val resolveOnRelease: () -> SwipeActionSerializable? = remember(stages, loopEnabledForRelease) {
        {
            val idx = stageIndexState.intValue
            if (idx == 0 || stages.isNullOrEmpty()) null
            else when {
                idx == stages.size + 1 && loopEnabledForRelease -> stages.last().action
                idx in 1..stages.size -> stages[idx - 1].action
                else -> null
            }
        }
    }

    val clear: () -> Unit = remember { { stageIndexState.intValue = 0 } }

    val maxIndex = when {
        stages.isNullOrEmpty() -> 0
        loopEnabledForRelease -> stages.size + 1
        else -> stages.size
    }
    val safeStageIndex = currentStageIndex.coerceIn(0, maxIndex)

    val isLoopOverPhase = !stages.isNullOrEmpty() &&
        loopEnabledForRelease &&
        safeStageIndex == stages.size + 1

    val currentStageAction: SwipeActionSerializable? = when {
        stages.isNullOrEmpty() -> null
        safeStageIndex == 0 -> null
        isLoopOverPhase -> stages.last().action
        safeStageIndex in 1..stages.size -> stages[safeStageIndex - 1].action
        else -> null
    }

    return CycleActionsState(
        isActive = isDragging && !stages.isNullOrEmpty(),
        currentStageIndex = safeStageIndex,
        currentStageAction = currentStageAction,
        isLoopOverPhase = isLoopOverPhase,
        resolveOnRelease = resolveOnRelease,
        clear = clear
    )
}
