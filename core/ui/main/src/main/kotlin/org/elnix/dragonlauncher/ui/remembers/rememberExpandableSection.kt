package org.elnix.dragonlauncher.ui.remembers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

data class ExpandableSectionState(
    val isExpanded: () -> Boolean,
    val enabled: () -> Boolean,
    val title: String,
    val toggle: () -> Unit,
)

@Composable
fun rememberExpandableSection(title: String, enabled: () -> Boolean = { true }): ExpandableSectionState {
    var isExpanded by remember { mutableStateOf(false) }

    return remember(title) {
        ExpandableSectionState(
            isExpanded = { isExpanded },
            enabled = enabled,
            title = title,
            toggle = { isExpanded = !isExpanded }
        )
    }
}
