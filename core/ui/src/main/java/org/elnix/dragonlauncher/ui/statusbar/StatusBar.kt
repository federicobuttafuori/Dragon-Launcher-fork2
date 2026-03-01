package org.elnix.dragonlauncher.ui.statusbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.serializables.StatusBarJson
import org.elnix.dragonlauncher.common.serializables.StatusBarSerializable
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.allStatusBarSerializable
import org.elnix.dragonlauncher.common.utils.Constants
import org.elnix.dragonlauncher.common.utils.Constants.Logging.STATUS_BAR_TAG
import org.elnix.dragonlauncher.common.utils.UiConstants.DragonShape
import org.elnix.dragonlauncher.common.utils.isValidDateFormat
import org.elnix.dragonlauncher.common.utils.isValidTimeFormat
import org.elnix.dragonlauncher.settings.stores.StatusBarJsonSettingsStore
import org.elnix.dragonlauncher.settings.stores.StatusBarSettingsStore
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.components.dragon.DragonButton
import org.elnix.dragonlauncher.ui.components.dragon.DragonColumnGroup
import org.elnix.dragonlauncher.ui.components.dragon.DragonIconButton
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.helpers.CustomActionSelector
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel
import org.elnix.dragonlauncher.ui.helpers.SwitchRow
import org.elnix.dragonlauncher.ui.modifiers.conditional
import org.elnix.dragonlauncher.ui.remembers.LocalStatusBarElements

@Composable
fun StatusBar(
    launchAction: ((SwipeActionSerializable) -> Unit)?,
) {
    val statusBarBackground by StatusBarSettingsStore.barBackgroundColor.asState()
    val statusBarText by StatusBarSettingsStore.barTextColor.asState()

    val leftStatusBarPadding by StatusBarSettingsStore.leftPadding.asState()
    val rightStatusBarPadding by StatusBarSettingsStore.rightPadding.asState()
    val topStatusBarPadding by StatusBarSettingsStore.topPadding.asState()
    val bottomStatusBarPadding by StatusBarSettingsStore.bottomPadding.asState()

    val elements = LocalStatusBarElements.current

    CompositionLocalProvider(
        LocalContentColor provides statusBarText
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(statusBarBackground)
                .padding(
                    start = leftStatusBarPadding.dp,
                    top = topStatusBarPadding.dp,
                    end = rightStatusBarPadding.dp,
                    bottom = bottomStatusBarPadding.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            elements.forEach { element ->

                logD(STATUS_BAR_TAG, "Element: $element")
                if (element !is StatusBarSerializable.Spacer) {
                    StatusBarItem(element, launchAction)
                } else {
                    val modifier = Modifier.conditional(
                        condition = element.width == -1,
                        block = { Modifier.weight(1f) },
                        fallback = { width(element.width.dp) }
                    )

                    Spacer(modifier)
                }
            }
        }
    }
}


private data class StatusBarElement(
    val id: Int,
    val item: StatusBarSerializable
)

@Composable
fun EditStatusBar() {
    val ctx = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val scope = rememberCoroutineScope()

    val statusBarBackground by StatusBarSettingsStore.barBackgroundColor.asState()
    val statusBarText by StatusBarSettingsStore.barTextColor.asState()

    val leftStatusBarPadding by StatusBarSettingsStore.leftPadding.asState()
    val rightStatusBarPadding by StatusBarSettingsStore.rightPadding.asState()
    val topStatusBarPadding by StatusBarSettingsStore.topPadding.asState()
    val bottomStatusBarPadding by StatusBarSettingsStore.bottomPadding.asState()

    val elements: SnapshotStateList<StatusBarElement> = remember { mutableStateListOf() }
    var selectedElementId by remember { mutableStateOf<Int?>(null) }


    suspend fun load() {
        elements.clear()

        val loadedElements = StatusBarJsonSettingsStore.jsonSetting.get(ctx)
        val elementsJson = StatusBarJson.decodeStatusBarElements(loadedElements)

        elementsJson.forEachIndexed { index, item ->
            elements.add(
                StatusBarElement(
                    id = index,
                    item = item
                )
            )
        }
    }

    // Load the elements of the status bar on first composition
    LaunchedEffect(Unit) {
        load()
    }

    fun save() {
        val elementsJson = StatusBarJson.encodeStatusBarElements(elements.map { it.item })
        scope.launch {
            StatusBarJsonSettingsStore.jsonSetting.set(ctx, elementsJson)
        }
    }

    fun addElement(element: StatusBarSerializable) {
        val newId = (elements.maxOfOrNull { it.id } ?: 0) + 1

        elements.add(
            StatusBarElement(
                id = newId,
                item = element
            )
        )
        save()
    }

    fun duplicateElement(element: StatusBarElement) {
        val index = elements.indexOfFirst { it.id == element.id }
        if (index == -1) return

        val newId = (elements.maxOfOrNull { it.id } ?: 0) + 1

        val copiedItem = when (val item = element.item) {
            is StatusBarSerializable.Time -> item.copy()
            is StatusBarSerializable.Date -> item.copy()
            is StatusBarSerializable.Bandwidth -> item.copy()
            is StatusBarSerializable.Notifications -> item.copy()
            is StatusBarSerializable.Connectivity -> item.copy()
            is StatusBarSerializable.Spacer -> item.copy()
            is StatusBarSerializable.Battery -> item.copy()
            is StatusBarSerializable.NextAlarm -> item.copy()
        }

        elements.add(
            index + 1,
            StatusBarElement(
                id = newId,
                item = copiedItem
            )
        )

        save()
    }

    fun removeElement(element: StatusBarElement) {
        elements -= element
        selectedElementId = null
        save()
    }

    fun updateElement(updated: StatusBarSerializable) {
        val index = elements.indexOfFirst { it.id == selectedElementId }
        if (index == -1) return

        elements[index] = elements[index].copy(item = updated)
        save()
    }

    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to ->
            if (from.index in elements.indices && to.index in 0..elements.size) {
                val tmp = elements.toMutableList()
                val item = tmp.removeAt(from.index)
                tmp.add(to.index, item)
                elements.clear()
                elements.addAll(tmp)
            }
        },
        onDragEnd = { _, _ ->
            // Persist changes
            save()
        }
    )

    CompositionLocalProvider(
        LocalContentColor provides statusBarText
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(MaterialTheme.colorScheme.surface, DragonShape)
                .reorderable(reorderState)
                .detectReorderAfterLongPress(reorderState)
                .background(statusBarBackground)
                .padding(
                    start = leftStatusBarPadding.dp,
                    top = topStatusBarPadding.dp,
                    end = rightStatusBarPadding.dp,
                    bottom = bottomStatusBarPadding.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            state = reorderState.listState
        ) {

            items(elements, key = { it.id }) { statusBarElement ->
                ReorderableItem(
                    state = reorderState,
                    key = statusBarElement.id
                ) { isDragging ->

                    val element = statusBarElement.item
                    val selected = statusBarElement.id == selectedElementId

                    val scale = animateFloatAsState(
                        targetValue = when {
                            isDragging && selected -> 1.2f
                            isDragging -> 1.3f
                            selected -> 0.9f
                            else -> 1f
                        }
                    )
                    val backgroundColor = animateColorAsState(
                        targetValue = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )


                    val borderColor = animateColorAsState(
                        targetValue = if (selected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )

                    LaunchedEffect(isDragging) {
                        if (isDragging) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }

                    Box(
                        modifier = Modifier
                            .scale(scale.value)
                            .border(1.dp, borderColor.value, DragonShape)
                            .clip(DragonShape)
                            .background(backgroundColor.value)
                            .clickable {
                                selectedElementId =
                                    if (selectedElementId == statusBarElement.id) null
                                    else statusBarElement.id
                            }
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        StatusBarItem(element)
                    }
                }
            }
        }

        AnimatedVisibility(selectedElementId != null) {
            elements.firstOrNull { it.id == selectedElementId }?.let { element ->
                DragonColumnGroup(
                    Modifier.fillMaxWidth()
                ) {

                    when (val item = element.item) {

                        is StatusBarSerializable.Bandwidth -> {
                            SwitchRow(
                                text = stringResource(R.string.merge_bandwidth),
                                subText = "",
                                state = item.merge,
                            ) {
                                updateElement(item.copy(merge = it))
                            }
                        }

                        is StatusBarSerializable.Connectivity -> {
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                SwitchRow(
                                    text = stringResource(R.string.show_airplane_mode),
                                    subText = "",
                                    state = item.showAirplaneMode,
                                ) {
                                    updateElement(item.copy(showAirplaneMode = it))
                                }
                                SwitchRow(
                                    text = stringResource(R.string.show_wifi),
                                    subText = "",
                                    state = item.showWifi,
                                ) {
                                    updateElement(item.copy(showWifi = it))
                                }
                                SwitchRow(
                                    text = stringResource(R.string.show_bluetooth),
                                    subText = "",
                                    state = item.showBluetooth,
                                ) {
                                    updateElement(item.copy(showBluetooth = it))
                                }
                                SwitchRow(
                                    text = stringResource(R.string.show_vpn),
                                    subText = "",
                                    state = item.showVpn,
                                ) {
                                    updateElement(item.copy(showVpn = it))
                                }
                                SwitchRow(
                                    text = stringResource(R.string.show_mobile_data),
                                    subText = "",
                                    state = item.showMobileData,
                                ) {
                                    updateElement(item.copy(showMobileData = it))
                                }
                                SwitchRow(
                                    text = stringResource(R.string.show_hotspot),
                                    subText = "",
                                    state = item.showHotspot,
                                ) {
                                    updateElement(item.copy(showHotspot = it))
                                }
                                SliderWithLabel(
                                    label = stringResource(R.string.connectivity_update_frequency),
                                    value = item.updateFrequency,
                                    valueRange = 1..60,
                                    onReset = { updateElement(item.copy(updateFrequency = 5)) }
                                ) {
                                    updateElement(item.copy(updateFrequency = it))
                                }
                            }
                        }

                        is StatusBarSerializable.Date -> {

                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text(
                                    text = stringResource(R.string.date_format_examples),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                OutlinedTextField(
                                    label = {
                                        Text(stringResource(R.string.date_format_title))
                                    },
                                    value = item.formatter,
                                    onValueChange = { newValue ->
                                        updateElement(item.copy(formatter = newValue))
                                    },
                                    singleLine = true,
                                    isError = !isValidDateFormat(item.formatter),
                                    supportingText = if (!isValidDateFormat(item.formatter)) {
                                        { Text(stringResource(R.string.invalid_format)) }
                                    } else null,
                                    placeholder = { Text("MMM dd") },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Restore,
                                            contentDescription = stringResource(R.string.reset),
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.clickable {
                                                updateElement(item.copy(formatter = "MMM dd"))
                                            }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = AppObjectsColors.outlinedTextFieldColors()
                                )

                                CustomActionSelector(
                                    currentAction = item.action,
                                    label = stringResource(R.string.clock_action),
                                    nullText = stringResource(R.string.opens_alarm_clock_app),
                                    onToggle = { updateElement(item.copy(action = null)) }
                                ) { updateElement(item.copy(action = it)) }
                            }
                        }

                        is StatusBarSerializable.Time -> {
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text(
                                    text = stringResource(R.string.time_format_examples),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                OutlinedTextField(
                                    label = { Text(stringResource(R.string.time_format_title)) },
                                    value = item.formatter,
                                    onValueChange = { newValue ->
                                        updateElement(item.copy(formatter = newValue))
                                    },
                                    singleLine = true,
                                    isError = !isValidTimeFormat(item.formatter),
                                    supportingText = if (!isValidTimeFormat(item.formatter)) {
                                        { Text(stringResource(R.string.invalid_format)) }
                                    } else null,
                                    placeholder = { Text("HH:mm:ss") },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Restore,
                                            contentDescription = stringResource(R.string.reset),
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.clickable {
                                                updateElement(item.copy(formatter = "HH:mm:ss"))
                                            }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = AppObjectsColors.outlinedTextFieldColors()
                                )
                            }
                        }

                        is StatusBarSerializable.Notifications -> {
                            SliderWithLabel(
                                label = stringResource(R.string.max_notification_icons),
                                value = item.maxIcons,
                                valueRange = 1..15,
                                onReset = { updateElement(item.copy(maxIcons = 5)) }
                            ) {
                                updateElement(item.copy(maxIcons = it))
                            }
                        }

                        is StatusBarSerializable.Spacer -> {
                            SliderWithLabel(
                                label = stringResource(R.string.width),
                                value = item.width,
                                valueRange = -1..30,
                                onReset = { updateElement(item.copy(width = -1)) }
                            ) {
                                updateElement(item.copy(width = it))
                            }
                        }

                        is StatusBarSerializable.Battery -> {
                            SwitchRow(
                                text = stringResource(R.string.show_percentage),
                                subText = stringResource(R.string.show_percentage_desc),
                                state = item.showPercentage,
                            ) {
                                updateElement(item.copy(showPercentage = it))
                            }
                        }

                        is StatusBarSerializable.NextAlarm -> {
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text(
                                    text = stringResource(R.string.time_format_examples),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                OutlinedTextField(
                                    label = { Text(stringResource(R.string.time_format_title)) },
                                    value = item.formatter,
                                    onValueChange = { newValue ->
                                        updateElement(item.copy(formatter = newValue))
                                    },
                                    singleLine = true,
                                    isError = !isValidTimeFormat(item.formatter),
                                    supportingText = if (!isValidTimeFormat(item.formatter)) {
                                        { Text(stringResource(R.string.invalid_format)) }
                                    } else null,
                                    placeholder = { Text("HH:mm") },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Restore,
                                            contentDescription = stringResource(R.string.reset),
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.clickable {
                                                updateElement(item.copy(formatter = "HH:mm"))
                                            }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = AppObjectsColors.outlinedTextFieldColors()
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { removeElement(element) },
                            colors = AppObjectsColors.cancelButtonColors()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.remove)
                            )
                            Text(stringResource(R.string.remove))
                        }

                        DragonIconButton(
                            onClick = {
                                duplicateElement(element)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = stringResource(R.string.copy)
                            )
                        }
                    }
                }
            }
        }



        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            allStatusBarSerializable.forEach {

                var showHelp by remember { mutableStateOf(false) }
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .border(1.dp, MaterialTheme.colorScheme.primary, DragonShape)
                            .clip(DragonShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .combinedClickable(
                                onLongClick = { showHelp = true },
                                onClick = { addElement(it) }
                            )
                            .padding(15.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        StatusBarItem(it)
                    }

                    DropdownMenu(
                        expanded = showHelp,
                        onDismissRequest = { showHelp = false },
                        containerColor = Color.Transparent,
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp
                    ) {
                        Text(
                            text = it::class.simpleName.toString(),
                            modifier = Modifier
                                .clip(DragonShape)
                                .background(MaterialTheme.colorScheme.background)
                                .padding(5.dp)
                        )
                    }
                }
            }
        }
    }

    DragonButton(
        onClick = {
            scope.launch {
                StatusBarJsonSettingsStore.jsonSetting.set(ctx, Constants.Settings.STATUS_BAR_TEMPLATE)
                load()
            }
        }
    ) {
        Text(stringResource(R.string.set_status_bar_template))
    }
}


@Composable
fun StatusBarItem(
    element: StatusBarSerializable,
    launchAction: ((SwipeActionSerializable) -> Unit)? = null,
) {
    when (element) {
        is StatusBarSerializable.Bandwidth -> {
            StatusBarBandwidth(element)
        }

        is StatusBarSerializable.Connectivity -> {
            StatusBarConnectivity(element)
        }

        is StatusBarSerializable.Date -> {
            StatusBarDate(
                element = element,
                onAction = launchAction,
            )
        }

        is StatusBarSerializable.Time -> {
            StatusBarTime(
                element = element,
                onAction = launchAction,
            )
        }

        is StatusBarSerializable.Notifications -> {
            StatusBarNotifications(element)
        }

        is StatusBarSerializable.Spacer -> {
            Text(stringResource(R.string.spacer))
        }

        is StatusBarSerializable.Battery -> {
            StatusBarBattery(element)
        }

        is StatusBarSerializable.NextAlarm -> {
            StatusBarNextAlarm(element)
        }
    }
}
