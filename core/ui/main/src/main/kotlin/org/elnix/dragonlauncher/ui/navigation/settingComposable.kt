package org.elnix.dragonlauncher.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable


fun NavGraphBuilder.settingComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        enterTransition = { slideFadeInFromRight() },
        exitTransition = { slideFadeOutToLeft() },
        popEnterTransition = { slideFadeInFromLeft() },
        popExitTransition = { slideFadeOutToRight() },
        content = content
    )
}
