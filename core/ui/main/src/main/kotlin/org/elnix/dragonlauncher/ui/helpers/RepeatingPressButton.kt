package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import org.elnix.dragonlauncher.common.utils.vibrate

@Composable
fun RepeatingPressButton(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    intervalMs: Long = 70L,
    startDelayMs: Long = 300L,
    vibrate: Long = startDelayMs/2,
    onPress: () -> Unit,
    content: @Composable () -> Unit
) {
    val ctx = LocalContext.current
    var isPressed by remember { mutableStateOf(false) }
    val currentOnPress by rememberUpdatedState(onPress)

    LaunchedEffect(isPressed, enabled) {
        if (enabled && isPressed) {
            // Immediate single press action
            currentOnPress()

            // Wait before repeat begins
            delay(startDelayMs)

            // If still pressed after delay, begin repeating
            while (isPressed) {
                currentOnPress()
                if (vibrate > 0) vibrate(ctx, vibrate)
                delay(intervalMs)
            }
        }
    }

    Box(
        modifier = Modifier.pointerInput(enabled) {
            if (!enabled) return@pointerInput

            detectTapGestures(
                onPress = {
                    val press = PressInteraction.Press(it)
                    interactionSource.emit(press)

                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false

                    interactionSource.emit(PressInteraction.Release(press))
                }
            )
        }
    ) {
        content()
    }
}
