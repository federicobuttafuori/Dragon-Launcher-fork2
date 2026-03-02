package org.elnix.dragonlauncher.common

import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable

data class FloatingAppObject(
    val id: Int,
    val appWidgetId: Int? = null,
    val nestId: Int?,
    val action: SwipeActionSerializable,
    val spanX: Float = 1f,
    val spanY: Float = 1f,
    val x: Float = 0f,
    val y: Float = 0f,
    val angle: Double = 0.0,
    val ghosted: Boolean? = false,
    val foreground: Boolean? = true
)
