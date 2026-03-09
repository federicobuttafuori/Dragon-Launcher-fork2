package org.elnix.dragonlauncher.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.ui.modifiers.conditional
import org.elnix.dragonlauncher.ui.modifiers.settingsGroup
import org.elnix.dragonlauncher.ui.remembers.ExpandableSectionState

@Composable
fun ExpandableSection(
    state: ExpandableSectionState,
    content: @Composable ColumnScope.() -> Unit
) {
    val enabled = state.enabled()

    // Expanded only if the expandable is also enabled
    val expanded = state.isExpanded() && enabled


    val expandedColor = MaterialTheme.colorScheme.surfaceVariant
    val collapsedColor = MaterialTheme.colorScheme.surface

    val rotationDegrees = remember {
        Animatable(0f)
    }

    val backgroundColor = remember {
        androidx.compose.animation.Animatable(
            collapsedColor
        )
    }

    // Collapse on disable
    LaunchedEffect(enabled) {
        if (state.isExpanded()) state.toggle()
    }

    LaunchedEffect(expanded) {
        rotationDegrees.animateTo(if (expanded) 90f else 0f)
    }
    LaunchedEffect(expanded) {
        backgroundColor.animateTo(if (expanded) expandedColor else collapsedColor)
    }

    Column(
        modifier = Modifier.settingsGroup(
            clickModifier = Modifier.conditional(!expanded && enabled) {
                clickable {
                    state.toggle()
                }
            },
            backgroundColor = backgroundColor.value,
            border = true,
            enabled = enabled
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier
                .settingsGroup(
                    clickModifier = Modifier.conditional(expanded) {
                        clickable {
                            state.toggle()
                        }
                    },
                    backgroundColor = Color.Transparent,
                    border = false,
                    enabled = enabled
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(state.title)

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = stringResource(R.string.expanded_chevron_indicator),
                modifier = Modifier
                    .rotate(rotationDegrees.value)
            )
        }

        AnimatedVisibility(
            visible = expanded
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                content()
            }
        }
    }
}
