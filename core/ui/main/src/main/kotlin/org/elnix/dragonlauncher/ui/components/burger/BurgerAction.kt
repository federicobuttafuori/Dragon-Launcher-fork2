package org.elnix.dragonlauncher.ui.components.burger

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

data class BurgerAction(
    val onClick: () -> Unit,
    val content: @Composable RowScope.() -> Unit
)
