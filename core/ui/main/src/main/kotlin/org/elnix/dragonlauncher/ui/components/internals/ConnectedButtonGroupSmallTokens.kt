package org.elnix.dragonlauncher.ui.components.internals

// Fuck license

import androidx.compose.ui.unit.dp


internal enum class ShapeKeyTokens {
    CornerExtraExtraLarge,
    CornerExtraLarge,
    CornerExtraLargeIncreased,
    CornerExtraLargeTop,
    CornerExtraSmall,
    CornerExtraSmallTop,
    CornerFull,
    CornerLarge,
    CornerLargeEnd,
    CornerLargeIncreased,
    CornerLargeStart,
    CornerLargeTop,
    CornerMedium,
    CornerNone,
    CornerSmall,
}


internal object ConnectedButtonGroupSmallTokens {
    val BetweenSpace = 2.0.dp
    val ContainerHeight = 40.0.dp
    val ContainerShape = ShapeKeyTokens.CornerFull
    val InnerCornerCornerSize = ShapeTokensClone.CornerValueSmall
    val PressedInnerCornerCornerSize = ShapeTokensClone.CornerValueExtraSmall
    val SelectedInnerCornerCornerSizePercent = 50.0f
}
