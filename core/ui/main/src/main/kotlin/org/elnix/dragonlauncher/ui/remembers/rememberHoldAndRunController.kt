package org.elnix.dragonlauncher.ui.remembers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable

/*  ─────────────  Hold & Run public state  ─────────────  */

/**
 * Snapshot of Hold & Run state returned per recomposition.
 *
 * @property firedThisGesture True once the action has been auto-fired during the current hold.
 *   Remains true until [clear] is called (on pointer-up or point exit).
 * @property clear Resets all state; call it from the overlay release guard.
 */
data class HoldAndRunState(
    val firedThisGesture: Boolean,
    val clear: () -> Unit
)

/*  ─────────────  Controller composable  ─────────────  */

/**
 * Composable controller for Hold & Run behavior.
 *
 * Fires [onFire] once with the current [SwipePointSerializable] after the configured
 * [SwipePointSerializable.holdAndRunDelayMs] of continuous hold on the same point.
 * If [SwipePointSerializable.holdAndRunAction] is set, the launched point uses that action;
 * otherwise the point’s main [SwipePointSerializable.action] is used.
 *
 * - If the finger exits the point before the delay elapses, the coroutine is cancelled
 *   because [currentAction] changes (or becomes null), restarting with a new key.
 * - When [currentAction] is already null (Live Nest is active), Hold & Run does not run.
 * - [onFire] is called exactly once per gesture; [firedThisGesture] stays true until [clear].
 *
 * @param currentAction The currently hovered point on the main nest, or null if inactive.
 * @param isDragging    True while a finger is on screen.
 * @param onFire        Lambda invoked on the UI thread when the hold delay elapses;
 *                      the caller is responsible for invoking [onLaunch] and suppressing release.
 */
@Composable
fun rememberHoldAndRunController(
    currentAction: SwipePointSerializable?,
    isDragging: Boolean,
    onFire: (SwipePointSerializable) -> Unit
): HoldAndRunState {

    val firedState = remember { mutableStateOf(false) }
    var firedThisGesture by firedState

    /*  ─────────────  Timer  ─────────────  */

    LaunchedEffect(currentAction?.id, isDragging) {
        // Always reset when the point changes or drag ends.
        firedThisGesture = false

        if (!isDragging || currentAction == null) return@LaunchedEffect

        val delayMs = currentAction.holdAndRunDelayMs?.toLong() ?: return@LaunchedEffect

        delay(delayMs)

        // Guard: still on the same point and not yet fired (safety for rapid transitions).
        if (!firedThisGesture) {
            firedThisGesture = true
            val override = currentAction.holdAndRunAction
            val toLaunch =
                if (override != null) currentAction.copy(action = override) else currentAction
            onFire(toLaunch)
        }
    }

    /*  ─────────────  Release helper  ─────────────  */

    val clear: () -> Unit = remember { { firedThisGesture = false } }

    return HoldAndRunState(
        firedThisGesture = firedThisGesture,
        clear = clear
    )
}
