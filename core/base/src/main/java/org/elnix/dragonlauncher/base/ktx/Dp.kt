package org.elnix.dragonlauncher.base.ktx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.toPixels(): Float {
    return value * LocalDensity.current.density
}

inline val Dp.px: Float
    @Composable
    get() = value * LocalDensity.current.density


/** Create a [Dp] using an [Float], using local density for consistent results across different density devices */
@Stable
inline val Float.toDp: Dp
    @Composable
    get() = with(LocalDensity.current) { this@toDp.toDp() }
