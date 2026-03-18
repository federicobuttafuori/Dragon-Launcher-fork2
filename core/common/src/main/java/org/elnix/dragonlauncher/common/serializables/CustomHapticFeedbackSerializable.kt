package org.elnix.dragonlauncher.common.serializables

import kotlinx.serialization.Serializable
import org.elnix.dragonlauncher.common.R


@Serializable
data class CustomHapticFeedbackSerializable(

    /**
     * The custom haptics, they are resolved one by one when performing the haptic feedback,
     * when the key is `true`, a vibration is performed, when it's `false` a delay is applied.
     * This way, the user can customize infinitely the haptic it wants for every situation
     * The value is the duration of the vibration/delay
     */
    val haptics: List<Pair<Boolean, Int>>
)


val hapticFeedbackSerializablePresets: List<Pair<Int, CustomHapticFeedbackSerializable>> = listOf(

    // Single short tap — like a subtle confirmation
    R.string.haptic_preset_single to CustomHapticFeedbackSerializable(
        listOf(
            true to 20
        )
    ),

    // Double tap — vibrate, pause, vibrate
    R.string.haptic_preset_double to CustomHapticFeedbackSerializable(
        listOf(
            true to 20,
            false to 80,
            true to 20
        )
    ),

    // Triple tap — three quick pulses
    R.string.haptic_preset_triple to CustomHapticFeedbackSerializable(
        listOf(
            true to 20,
            false to 60,
            true to 20,
            false to 60,
            true to 20
        )
    ),

    // Heavy thud — one long strong buzz
    R.string.haptic_preset_heavy to CustomHapticFeedbackSerializable(
        listOf(
            true to 80
        )
    ),

    // Heartbeat — short, pause, long
    R.string.haptic_preset_heartbeat to CustomHapticFeedbackSerializable(
        listOf(
            true to 20,
            false to 40,
            true to 60
        )
    ),

    // Error rattle — three uneven jolts
    R.string.haptic_preset_error to CustomHapticFeedbackSerializable(
        listOf(
            true to 50,
            false to 30,
            true to 50,
            false to 30,
            true to 100
        )
    ),

    // Gentle tick — barely-there nudge
    R.string.haptic_preset_tick to CustomHapticFeedbackSerializable(
        listOf(
            true to 8
        )
    ),

    // Success pop — short then long, like a reward
    R.string.haptic_preset_success to CustomHapticFeedbackSerializable(
        listOf(
            true to 15,
            false to 50,
            true to 40
        )
    ),

    // Buzz roll — rapid-fire tiny pulses
    R.string.haptic_preset_buzz_roll to CustomHapticFeedbackSerializable(
        listOf(
            true to 10,
            false to 20,
            true to 10,
            false to 20,
            true to 10,
            false to 20,
            true to 10
        )
    ),

    // Long press confirm — slow build, short finish
    R.string.haptic_preset_long_press to CustomHapticFeedbackSerializable(
        listOf(
            true to 60,
            false to 100,
            true to 20
        )
    ),
)
