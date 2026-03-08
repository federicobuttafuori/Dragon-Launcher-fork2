@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.settings.customization

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Polyline
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.dummySwipePoint
import org.elnix.dragonlauncher.common.utils.SETTINGS
import org.elnix.dragonlauncher.settings.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore.appIconOverlaySize
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore.appLabelIconOverlayTopPadding
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore.appLabelOverlaySize
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore.showLaunchingAppIcon
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore.showLaunchingAppLabel
import org.elnix.dragonlauncher.ui.components.AppPreviewTitle
import org.elnix.dragonlauncher.ui.components.ExpandableSection
import org.elnix.dragonlauncher.ui.components.TextDivider
import org.elnix.dragonlauncher.ui.components.dragon.DragonColumnGroup
import org.elnix.dragonlauncher.ui.components.settings.SettingsSlider
import org.elnix.dragonlauncher.ui.components.settings.SettingsSwitchRow
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.dialogs.FontPickerDialog
import org.elnix.dragonlauncher.ui.helpers.HoldToActivateArc
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsItem
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.ui.remembers.LocalIcons
import org.elnix.dragonlauncher.ui.remembers.rememberExpandableSection


@Composable
fun AppearanceTab(
    navController: NavController,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val icons = LocalIcons.current
    val scope = rememberCoroutineScope()

    // Top overlay things
    val showLaunchingAppLabel by showLaunchingAppLabel.asState()
    val showLaunchingAppIcon by showLaunchingAppIcon.asState()
    val appLabelIconOverlayTopPadding by appLabelIconOverlayTopPadding.asState()
    val appLabelOverlaySize by appLabelOverlaySize.asState()
    val appIconOverlaySize by appIconOverlaySize.asState()
    val showAllActionsOnCurrentCircle by UiSettingsStore.showAllActionsOnCurrentCircle.asState()

    var showFontPicker by remember { mutableStateOf(false) }

    val topOverlaySettingsState = rememberExpandableSection(stringResource(R.string.app_preview_settings))
    val holdExpandableSectionState = rememberExpandableSection(stringResource(R.string.hold_settings))


    var isDraggingAppPreviewOverlays by remember { mutableStateOf(false) }
    var demoIcon by remember { mutableStateOf(icons.keys.random()) }

    LaunchedEffect(isDraggingAppPreviewOverlays) {
        // Changed the icon only when the user stops dragging, to prevent UI animations overhead
        demoIcon = icons.keys.random()
    }


    SettingsLazyHeader(
        title = stringResource(R.string.appearance),
        onBack = onBack,
        helpText = stringResource(R.string.appearance_tab_text),
        onReset = {
            scope.launch {
                UiSettingsStore.resetAll(ctx)
            }
        }
    ) {

        item {
            SettingsItem(
                title = stringResource(R.string.color_selector),
                icon = Icons.Default.ColorLens
            ) { navController.navigate(SETTINGS.COLORS) }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.wallpaper),
                icon = Icons.Default.Wallpaper
            ) { navController.navigate(SETTINGS.WALLPAPER) }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.icon_pack),
                icon = Icons.Default.Palette
            ) { navController.navigate(SETTINGS.ICON_PACK) }
        }


        item {
            SettingsItem(
                title = stringResource(R.string.status_bar),
                icon = Icons.Default.SignalCellular4Bar
            ) { navController.navigate(SETTINGS.STATUS_BAR) }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.theme_selector),
                icon = Icons.Default.Style
            ) { navController.navigate(SETTINGS.THEME) }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.font_selector),
                description = stringResource(R.string.font_selector_desc),
                icon = Icons.Default.TextFields
            ) {
                showFontPicker = true
            }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.angle_line),
                icon = Icons.Default.Polyline
            ) { navController.navigate(SETTINGS.ANGLE_LINE_EDIT) }
        }

        item { TextDivider(stringResource(R.string.app_display)) }


        item {
            SettingsSwitchRow(
                setting = UiSettingsStore.fullScreen,
                title = stringResource(R.string.fullscreen_app),
                description = stringResource(R.string.fullscreen_description)
            )
        }


        item {
            ExpandableSection(holdExpandableSectionState) {


                val holdDelayBeforeStartingLongClickSettings by BehaviorSettingsStore.holdDelayBeforeStartingLongClickSettings.asState()
                val longCLickSettingsDuration by BehaviorSettingsStore.longCLickSettingsDuration.asState()
                val holdToActivateSettingsRadius by BehaviorSettingsStore.holdToActivateSettingsRadius.asState()
                val holdToActivateSettingsStroke by BehaviorSettingsStore.holdToActivateSettingsStroke.asState()
                val holdToActivateSettingsTolerance by BehaviorSettingsStore.holdToActivateSettingsTolerance.asState()
                val showToleranceOnMainScreen by BehaviorSettingsStore.showToleranceOnMainScreen.asState()
                val rgbLoading by UiSettingsStore.rgbLoading.asState()
                val defaultColor = Color.Red


                val progress = remember { Animatable(0f) }

                LaunchedEffect(
                    holdDelayBeforeStartingLongClickSettings,
                    longCLickSettingsDuration
                ) {
                    while (true) {
                        progress.snapTo(0f)

                        delay(holdDelayBeforeStartingLongClickSettings.toLong())

                        progress.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(
                                durationMillis = longCLickSettingsDuration,
                                easing = LinearEasing
                            )
                        )
                    }
                }


                var boxSize by remember { mutableStateOf(IntSize.Zero) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((holdToActivateSettingsRadius * 2).dp)
                        .onSizeChanged { boxSize = it },
                    contentAlignment = Alignment.Center
                ) {
                    val center = Offset(
                        x = boxSize.width / 2f,
                        y = boxSize.height / 2f - 15f
                    )

                    HoldToActivateArc(
                        center = center,
                        progress = progress.value,
                        radius = holdToActivateSettingsRadius,
                        stroke = holdToActivateSettingsStroke,
                        rgbLoading = rgbLoading,
                        defaultColor = defaultColor,
                        showHoldTolerance = if (showToleranceOnMainScreen) {
                            { holdToActivateSettingsTolerance }
                        } else null
                    )
                }

                SettingsSlider(
                    setting = BehaviorSettingsStore.longCLickSettingsDuration,
                    title = stringResource(R.string.long_click_settings_duration),
                    description = stringResource(R.string.long_click_settings_duration_desc),
                    valueRange = 200..5000
                )

                SettingsSlider(
                    setting = BehaviorSettingsStore.holdDelayBeforeStartingLongClickSettings,
                    title = stringResource(R.string.hold_delay_before_starting_long_click_settings),
                    description = stringResource(R.string.hold_delay_before_starting_long_click_settings_desc),
                    valueRange = 200..2000
                )

                SettingsSlider(
                    setting = BehaviorSettingsStore.holdToActivateSettingsRadius,
                    title = stringResource(R.string.hold_to_activate_radius),
                    description = stringResource(R.string.hold_to_activate_radius_desc),
                    valueRange = 10..300
                )

                SettingsSlider(
                    setting = BehaviorSettingsStore.holdToActivateSettingsStroke,
                    title = stringResource(R.string.hold_to_activate_stroke),
                    description = stringResource(R.string.hold_to_activate_stroke_desc),
                    valueRange = 1..50
                )

                SettingsSlider(
                    setting = BehaviorSettingsStore.holdToActivateSettingsTolerance,
                    title = stringResource(R.string.hold_to_activate_tolerance),
                    description = stringResource(R.string.hold_to_activate_tolerance_desc),
                    valueRange = 1f..200f
                )

                SettingsSwitchRow(
                    setting = BehaviorSettingsStore.showToleranceOnMainScreen,
                    title = stringResource(R.string.show_tolerance_on_main_screen),
                    description = stringResource(R.string.show_tolerance_on_main_screen_desc),
                )

                SettingsSwitchRow(
                    setting = UiSettingsStore.rgbLoading,
                    title = stringResource(R.string.rgb_loading_settings),
                    description = stringResource(R.string.rgb_loading_description)
                )
            }
        }


        item {
            ExpandableSection(topOverlaySettingsState) {

                SettingsSwitchRow(
                    setting = UiSettingsStore.showLaunchingAppLabel,
                    title = stringResource(R.string.show_launching_app_label),
                    description = stringResource(R.string.show_launching_app_label_description)
                )

                SettingsSwitchRow(
                    setting = UiSettingsStore.showLaunchingAppIcon,
                    title = stringResource(R.string.show_launching_app_icon),
                    description = stringResource(R.string.show_launching_app_icon_description)
                )

                SettingsSlider(
                    setting = UiSettingsStore.appLabelIconOverlayTopPadding,
                    title = stringResource(R.string.app_label_icon_overlay_top_padding),
                    valueRange = 0..1000,
                    color = MaterialTheme.colorScheme.primary,
                    onDragStateChange = { isDraggingAppPreviewOverlays = it }
                )

                SettingsSlider(
                    setting = UiSettingsStore.appLabelOverlaySize,
                    title = stringResource(R.string.app_label_overlay_size),
                    valueRange = 0..100,
                    color = MaterialTheme.colorScheme.primary,
                    onDragStateChange = { isDraggingAppPreviewOverlays = it }
                )

                SettingsSlider(
                    setting = UiSettingsStore.appIconOverlaySize,
                    title = stringResource(R.string.app_icon_overlay_size),
                    valueRange = 0..400,
                    color = MaterialTheme.colorScheme.primary,
                    onDragStateChange = { isDraggingAppPreviewOverlays = it }
                )
            }
        }


        item {
            DragonColumnGroup {
                SettingsSwitchRow(
                    setting = UiSettingsStore.showAppLaunchingPreview,
                    title = stringResource(R.string.show_app_launch_preview),
                    description = stringResource(R.string.show_app_launch_preview_description)
                )

                SettingsSwitchRow(
                    setting = UiSettingsStore.showCirclePreview,
                    title = stringResource(R.string.show_app_circle_preview),
                    description = stringResource(R.string.show_app_circle_preview_description)
                )

                SettingsSwitchRow(
                    setting = UiSettingsStore.showAllActionsOnCurrentCircle,
                    title = stringResource(R.string.show_all_actions_on_current_circle),
                    description = stringResource(R.string.show_all_actions_on_current_circle_description)
                ) {
                    if (!it) {
                        scope.launch {
                            UiSettingsStore.showAllActionsOnCurrentNest.set(ctx, false)
                        }
                    }
                }

                SettingsSwitchRow(
                    setting = UiSettingsStore.showAllActionsOnCurrentNest,
                    enabled = showAllActionsOnCurrentCircle,
                    title = stringResource(R.string.show_all_actions_on_current_nest),
                    description = stringResource(R.string.show_all_actions_on_current_nest_desc)
                )
            }
        }

        item {
            DragonColumnGroup {

                /* If the line is rgb (computed via the angle) or uses the line color from settings */
                SettingsSwitchRow(
                    setting = UiSettingsStore.rgbLine,
                    title = stringResource(R.string.rgb_line_selector),
                    description = stringResource(R.string.rgb_line_selector_description)
                )

                SettingsSwitchRow(
                    setting = UiSettingsStore.showAppPreviewIconCenterStartPosition,
                    title = stringResource(R.string.show_app_icon_start_drag_position),
                    description = stringResource(R.string.show_app_icon_start_drag_position_description)
                )

                SettingsSwitchRow(
                    setting = UiSettingsStore.linePreviewSnapToAction,
                    title = stringResource(R.string.line_preview_snap_to_action),
                    description = stringResource(R.string.line_preview_snap_to_action_description)
                )
            }
        }

        item {
            SettingsSlider(
                setting = UiSettingsStore.maxNestsDepth,
                title = stringResource(R.string.depth),
                description = stringResource(R.string.depth_desc),
                valueRange = 1..10
            )
        }
    }

    if (isDraggingAppPreviewOverlays) {
        AppPreviewTitle(
            point = dummySwipePoint(SwipeActionSerializable.OpenRecentApps).copy(
                customName = "Preview",
                id = demoIcon
            ),
            topPadding = appLabelIconOverlayTopPadding.dp,
            labelSize = appLabelOverlaySize,
            iconSize = appIconOverlaySize,
            showLabel = showLaunchingAppLabel,
            showIcon = showLaunchingAppIcon
        )
    }

    if (showFontPicker) {
        FontPickerDialog { showFontPicker = false }
    }
}
