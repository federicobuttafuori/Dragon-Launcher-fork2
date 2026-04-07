package org.elnix.dragonlauncher.ui.animations

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut

val enterTransition =
    fadeIn(
        animationSpec = tween(
            durationMillis = 220,
            easing = FastOutSlowInEasing
        )
    ) +
            scaleIn(
                initialScale = 0.95f,
                animationSpec = tween(
                    durationMillis = 220,
                    easing = FastOutSlowInEasing
                )
            )


val exitTransition =
    fadeOut(
        animationSpec = tween(
            durationMillis = 150,
            easing = LinearOutSlowInEasing
        )
    ) +
            scaleOut(
                targetScale = 0.95f,
                animationSpec = tween(
                    durationMillis = 150,
                    easing = LinearOutSlowInEasing
                )
            )
