@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.settings.customization

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.base.ktx.px
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.utils.UiCircle
import org.elnix.dragonlauncher.common.utils.UiConstants.DragonShape
import org.elnix.dragonlauncher.common.utils.vibrate
import org.elnix.dragonlauncher.enumsui.NestEditMode
import org.elnix.dragonlauncher.enumsui.NestEditMode.DRAG
import org.elnix.dragonlauncher.enumsui.NestEditMode.HAPTIC
import org.elnix.dragonlauncher.enumsui.NestEditMode.MIN_ANGLE
import org.elnix.dragonlauncher.enumsui.NestEditMode.RADIUS
import org.elnix.dragonlauncher.settings.stores.SwipeMapSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.ui.components.generic.ActionRow
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.defaultDragDistance
import org.elnix.dragonlauncher.ui.defaultHapticFeedback
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel
import org.elnix.dragonlauncher.ui.helpers.SwitchRow
import org.elnix.dragonlauncher.ui.helpers.nests.circlesSettingsOverlay
import org.elnix.dragonlauncher.ui.helpers.nests.swipeDefaultParams
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.ui.remembers.LocalNests

@Composable
fun NestEditingScreen(
    nestId: Int?,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val nests = LocalNests.current

    if (nestId == null) return
    val currentNest = nests.find { it.id == nestId } ?: return


    val angleColor = MaterialTheme.colorScheme.tertiary

    val drawParams = swipeDefaultParams(
        backgroundColor = MaterialTheme.colorScheme.background
    )

    val subNestDefaultRadius by SwipeMapSettingsStore.subNestDefaultRadius.asState()
    var tempRadius by remember { mutableStateOf(currentNest.nestRadius) }


    val dragDistancesState = remember(currentNest.id) {
        mutableStateMapOf<Int, Int>().apply {
            putAll(currentNest.dragDistances)
        }
    }

    val hapticState = remember(currentNest.id) {
        mutableStateMapOf<Int, Int>().apply {
            putAll(currentNest.haptic)
        }
    }

    val minAngleState = remember(currentNest.id) {
        mutableStateMapOf<Int, Int>().apply {
            putAll(currentNest.minAngleActivation)
        }
    }

    val circlesRealSize = dragDistancesState.map { (id, radius) ->
        UiCircle(
            id = id,
            radius = radius.toFloat()
        )
    }

    val circlesPreview = dragDistancesState.map { (id, radius) ->
        UiCircle(
            id = id,
            radius = (tempRadius ?: subNestDefaultRadius).dp.px
        )
    }

    var showSmallPreview by remember { mutableStateOf(false) }
    var currentEditMode by remember { mutableStateOf(DRAG) }
    var pendingNestUpdate by remember { mutableStateOf<List<CircleNest>?>(null) }

    /**
     * Saving system, the nests are immutable, they are saved using a pending value, that
     * asynchronously saves the nests in the datastore
     */
    LaunchedEffect(pendingNestUpdate) {
        pendingNestUpdate?.let { nests ->
            SwipeSettingsStore.saveNests(ctx, nests)
            pendingNestUpdate = null
        }
    }


    fun commitDragDistances(state: Map<Int, Int>) {
        pendingNestUpdate = nests.map { nest ->
            if (nest.id == nestId) {
                nest.copy(dragDistances = state.toMap())
            } else nest
        }
    }

    fun commitHaptic(state: Map<Int, Int>) {
        pendingNestUpdate = nests.map { nest ->
            if (nest.id == nestId) {
                nest.copy(haptic = state.toMap())
            } else nest
        }
    }

    fun commitAngle(state: Map<Int, Int>) {
        pendingNestUpdate = nests.map { nest ->
            if (nest.id == nestId) {
                nest.copy(minAngleActivation = state.toMap())
            } else nest
        }
    }




    SettingsLazyHeader(
        title = stringResource(R.string.dragging_distance_selection),
        onBack = onBack,
        helpText = "Help",
        onReset = {
            pendingNestUpdate = nests.filter { it.id != nestId }
        },

        content = {

            Canvas(
                Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {

                circlesSettingsOverlay(
                    drawParams = drawParams,
                    center = Offset(
                        x = 5.dp.toPx(),
                        y = 25.dp.toPx()
                    ),
                    depth = 1,
                    circles = circlesPreview,
                    selectedPoint = null,
                    nestId = nestId,
                    preventBgErasing = true
                )


                circlesSettingsOverlay(
                    drawParams = drawParams,
                    center = center,
                    depth = 1,
                    circles = circlesRealSize,
                    selectedPoint = null,
                    nestId = nestId,
                    preventBgErasing = true
                )


                // Show the min angle to activate
                circlesRealSize.forEach { circle ->
                    val arcRadius = circle.radius + 10

                    val rect = Rect(
                        center.x - arcRadius,
                        center.y - arcRadius,
                        center.x + arcRadius,
                        center.y + arcRadius
                    )

                    drawArc(
                        color = angleColor,
                        startAngle = -90f,
                        sweepAngle = minAngleState[circle.id]?.toFloat() ?: 0f,
                        useCenter = false,
                        topLeft = rect.topLeft,
                        size = Size(rect.width, rect.height),
                        style = Stroke(width = 3f)
                    )
                }
            }


            ActionRow(
                actions = NestEditMode.entries,
                selectedView = currentEditMode,
                actionIcon = { it.icon }
            ) {
                currentEditMode = it
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(DragonShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.primary, DragonShape)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                when (currentEditMode) {
                    DRAG -> {
                        dragDistancesState.toSortedMap().forEach { (index, distance) ->
                            SliderWithLabel(
                                label = if (index == -1) "${stringResource(R.string.cancel_zone)} ->"
                                else "${stringResource(R.string.circle)}: $index ->",
                                value = distance,
                                valueRange = 0..1000,
                                showValue = true,
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                                onReset = {
                                    dragDistancesState[index] = defaultDragDistance(index)
                                    commitDragDistances(dragDistancesState)
                                },
                                onDragStateChange = { isDragging ->
                                    if (!isDragging) {
                                        commitDragDistances(dragDistancesState)
                                    }
                                }
                            ) { newValue ->
                                dragDistancesState[index] = newValue
                            }
                        }
                    }

                    HAPTIC -> {
                        // Keep drag distance state here cause haptic may be empty dues to how it is handled
                        dragDistancesState.toSortedMap().filter { it.key != -1 }
                            .forEach { (index, _) ->
                                val milliseconds =
                                    hapticState[index] ?: defaultHapticFeedback(index)
                                SliderWithLabel(
                                    label = "${stringResource(R.string.haptic_feedback)}: $index ->",
                                    value = milliseconds,
                                    valueRange = 0..300,
                                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                                    onReset = {
                                        hapticState[index] = defaultHapticFeedback(index)
                                        commitHaptic(hapticState)
                                    },
                                    onDragStateChange = { isDragging ->
                                        if (!isDragging) {
                                            commitHaptic(hapticState)
                                        }

                                        if (!isDragging && milliseconds > 0) {
                                            vibrate(ctx, milliseconds.toLong())
                                        }
                                    }
                                ) { newValue ->
                                    hapticState[index] = newValue
                                }
                            }
                    }

                    MIN_ANGLE -> {
                        dragDistancesState.toSortedMap().filter { it.key != -1 }
                            .forEach { (index, _) ->
                                val angle = minAngleState[index] ?: 0
                                SliderWithLabel(
                                    label = "${stringResource(R.string.min_angle_to_activate)}: $index ->",
                                    value = angle,
                                    valueRange = 0..360,
                                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                                    onReset = {
                                        minAngleState[index] = 0
                                        commitAngle(minAngleState)
                                    },
                                    onDragStateChange = { isDragging ->
                                        if (!isDragging) {
                                            commitAngle(minAngleState)
                                        }
                                    }
                                ) { newValue ->
                                    minAngleState[index] = newValue
                                }
                            }
                    }

                    // Well in this tab I'll just put whatever settings I can put
                    RADIUS -> {
                        SliderWithLabel(
                            label = stringResource(R.string.nest_radius),
                            value = tempRadius ?: subNestDefaultRadius,
                            valueRange = 0..50,
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                            onReset = {
                                pendingNestUpdate = nests.map {
                                    if (it.id == nestId) it.copy(nestRadius = null)
                                    else it
                                }
                                tempRadius = subNestDefaultRadius
                            },
                            onDragStateChange = { isDragging ->
                                if (!isDragging) {
                                    pendingNestUpdate = nests.map {
                                        if (it.id == nestId) it.copy(nestRadius = tempRadius)
                                        else it
                                    }
                                }
                            }
                        ) { newValue -> tempRadius = newValue }

                        // Used to control whether the nest displays its circle individually or not
                        SwitchRow(
                            state = currentNest.showCircle ?: drawParams.showCircle,
                            text = stringResource(R.string.show_circle)
                        ) { showCircle ->

                            pendingNestUpdate = nests.map {
                                if (it.id == nestId) it.copy(showCircle = showCircle)
                                else it
                            }
                        }
                    }
                }
            }
        }
    )
}
