package org.elnix.dragonlauncher.ui.components.internals

// Fuck license

/**
 * My own copy of the file, because They put values as internal
 */

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

internal object ShapeTokensClone {
    val CornerExtraExtraLarge = RoundedCornerShape(48.0.dp)
    val CornerExtraLarge = RoundedCornerShape(28.0.dp)
    val CornerExtraLargeIncreased = RoundedCornerShape(32.0.dp)
    val CornerExtraLargeTop =
        RoundedCornerShape(
            topStart = 28.0.dp,
            topEnd = 28.0.dp,
            bottomEnd = 0.0.dp,
            bottomStart = 0.0.dp,
        )
    val CornerExtraSmall = RoundedCornerShape(4.0.dp)
    val CornerExtraSmallTop =
        RoundedCornerShape(
            topStart = 4.0.dp,
            topEnd = 4.0.dp,
            bottomEnd = 0.0.dp,
            bottomStart = 0.0.dp,
        )
    val CornerFull = CircleShape
    val CornerLarge = RoundedCornerShape(16.0.dp)
    val CornerLargeEnd =
        RoundedCornerShape(
            topStart = 0.0.dp,
            topEnd = 16.0.dp,
            bottomEnd = 16.0.dp,
            bottomStart = 0.0.dp,
        )
    val CornerLargeIncreased = RoundedCornerShape(20.0.dp)
    val CornerLargeStart =
        RoundedCornerShape(
            topStart = 16.0.dp,
            topEnd = 0.0.dp,
            bottomEnd = 0.0.dp,
            bottomStart = 16.0.dp,
        )
    val CornerLargeTop =
        RoundedCornerShape(
            topStart = 16.0.dp,
            topEnd = 16.0.dp,
            bottomEnd = 0.0.dp,
            bottomStart = 0.0.dp,
        )
    val CornerMedium = RoundedCornerShape(12.0.dp)
    val CornerNone = RectangleShape
    val CornerSmall = RoundedCornerShape(8.0.dp)
    val CornerValueExtraExtraLarge = CornerSize(48.0.dp)
    val CornerValueExtraLarge = CornerSize(28.0.dp)
    val CornerValueExtraLargeIncreased = CornerSize(32.0.dp)
    val CornerValueExtraSmall = CornerSize(4.0.dp)
    val CornerValueLarge = CornerSize(16.0.dp)
    val CornerValueLargeIncreased = CornerSize(20.0.dp)
    val CornerValueMedium = CornerSize(12.0.dp)
    val CornerValueNone = CornerSize(0.0.dp)
    val CornerValueSmall = CornerSize(8.0.dp)
}
