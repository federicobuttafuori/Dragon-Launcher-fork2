package org.elnix.dragonlauncher.common.utils

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.serializables.CustomGlow
import org.elnix.dragonlauncher.common.serializables.CustomObjectSerializable
import org.elnix.dragonlauncher.common.serializables.IconShape

object UiConstants {

    val DRAGON_SHAPE_CORNER_DP = 12.dp

    val CIRCLE_SHAPE_CORNER_DP = 50.dp

    val DragonShape = RoundedCornerShape(DRAGON_SHAPE_CORNER_DP)

    val defaultLineCustomObject = CustomObjectSerializable(
        stroke = 0.3f,
        color = null, // RGB Color according to the angle
        glow = CustomGlow(
            radius = 5f
        ),

        /** Not used for the line as it goes from `start` to `end` */
        shape = null,
        size = null,

        eraseBackground = false
    )


    val defaultAngleCustomObject = CustomObjectSerializable(
        stroke = 3f,
        color = null, // RGB Color according to the angle
        glow = CustomGlow(
            radius = 20f
        ),
        shape = IconShape.Circle,
        size = 20f,
        eraseBackground = false
    )

    val defaultStartCustomObject = CustomObjectSerializable(
        stroke = 4f,
        color = null, // RGB Color according to the angle
        glow = CustomGlow(
            radius = 40f
        ),
        shape = IconShape.Circle,
        size = 75f,
        eraseBackground = true
    )

    val defaultEndCustomObject = CustomObjectSerializable(
        stroke = 4f,
        color = null, // RGB Color according to the angle
        glow = CustomGlow(
            color = null,
            radius = 15f
        ),
        shape = IconShape.Circle,
        size = 10f,
        eraseBackground = false
    )
}
