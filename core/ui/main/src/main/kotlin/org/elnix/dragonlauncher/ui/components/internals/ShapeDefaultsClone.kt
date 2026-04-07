package org.elnix.dragonlauncher.ui.components.internals

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Shapes
import org.elnix.dragonlauncher.ui.components.internals.ShapeDefaultsClone.CornerExtraLarge
import org.elnix.dragonlauncher.ui.components.internals.ShapeDefaultsClone.CornerLarge
import org.elnix.dragonlauncher.ui.components.internals.ShapeDefaultsClone.ExtraLarge
import org.elnix.dragonlauncher.ui.components.internals.ShapeDefaultsClone.Large


/**
 * Contains the default values used by [Shapes]
 * My own copy because it's internal in library
 * */
object ShapeDefaultsClone {
    /** Extra small sized corner shape */
    val ExtraSmall: CornerBasedShape = ShapeTokensClone.CornerExtraSmall

    /** Small sized corner shape */
    val Small: CornerBasedShape = ShapeTokensClone.CornerSmall

    /** Medium sized corner shape */
    val Medium: CornerBasedShape = ShapeTokensClone.CornerMedium

    /** Large sized corner shape */
    val Large: CornerBasedShape = ShapeTokensClone.CornerLarge

    @ExperimentalMaterial3ExpressiveApi
            /** Large sized corner shape, slightly larger than [Large] */
    val LargeIncreased: CornerBasedShape = ShapeTokensClone.CornerLargeIncreased

    /** Extra large sized corner shape */
    val ExtraLarge: CornerBasedShape = ShapeTokensClone.CornerExtraLarge

    @ExperimentalMaterial3ExpressiveApi
            /** Extra large sized corner shape, slightly larger than [ExtraLarge] */
    val ExtraLargeIncreased: CornerBasedShape = ShapeTokensClone.CornerExtraLargeIncreased

    @ExperimentalMaterial3ExpressiveApi
            /** An extra extra large (XXL) sized corner shape */
    val ExtraExtraLarge: CornerBasedShape = ShapeTokensClone.CornerExtraExtraLarge

    /** A non-rounded corner size */
    internal val CornerNone: CornerSize = ShapeTokensClone.CornerValueNone

    /** An extra small rounded corner size */
    internal val CornerExtraSmall: CornerSize = ShapeTokensClone.CornerValueExtraSmall

    /** A small rounded corner size */
    internal val CornerSmall: CornerSize = ShapeTokensClone.CornerValueSmall

    /** A medium rounded corner size */
    internal val CornerMedium: CornerSize = ShapeTokensClone.CornerValueMedium

    /** A large rounded corner size */
    internal val CornerLarge: CornerSize = ShapeTokensClone.CornerValueLarge

    /** A large rounded corner size, slightly larger than [CornerLarge] */
    internal val CornerLargeIncreased: CornerSize = ShapeTokensClone.CornerValueLargeIncreased

    /** An extra large rounded corner size */
    internal val CornerExtraLarge: CornerSize = ShapeTokensClone.CornerValueExtraLarge

    /** An extra large rounded corner size, slightly larger than [CornerExtraLarge] */
    internal val CornerExtraLargeIncreased: CornerSize = ShapeTokensClone.CornerValueExtraLargeIncreased

    /** An extra extra large (XXL) rounded corner size */
    internal val CornerExtraExtraLarge: CornerSize = ShapeTokensClone.CornerValueExtraExtraLarge

    /** A fully rounded corner size */
    internal val CornerFull: CornerSize = CornerSize(100)
}

