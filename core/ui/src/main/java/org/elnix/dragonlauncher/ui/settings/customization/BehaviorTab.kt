@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.settings.customization

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.common.serializables.allShapesWithoutRandom
import org.elnix.dragonlauncher.common.utils.SETTINGS
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.enumsui.LockMethod
import org.elnix.dragonlauncher.settings.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.settings.stores.DebugSettingsStore
import org.elnix.dragonlauncher.settings.stores.PrivateAppsSettingsStore
import org.elnix.dragonlauncher.settings.stores.PrivateSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.components.ExpandableSection
import org.elnix.dragonlauncher.ui.components.dragon.DragonColumnGroup
import org.elnix.dragonlauncher.ui.components.settings.SettingsSlider
import org.elnix.dragonlauncher.ui.components.settings.SettingsSwitchRow
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.dialogs.CustomAlertDialog
import org.elnix.dragonlauncher.ui.dialogs.PinSetupDialog
import org.elnix.dragonlauncher.ui.dialogs.PinUnlockDialog
import org.elnix.dragonlauncher.ui.helpers.CustomActionSelector
import org.elnix.dragonlauncher.ui.helpers.SecurityHelper
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel
import org.elnix.dragonlauncher.ui.helpers.findFragmentActivity
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsItem
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.ui.remembers.LocalAppLifecycleViewModel
import org.elnix.dragonlauncher.ui.remembers.rememberExpandableSection


@Suppress("VariableNeverRead")
@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun BehaviorTab(
    navController: NavController,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val appLifecycleViewModel = LocalAppLifecycleViewModel.current

    val scope = rememberCoroutineScope()

    val backAction by BehaviorSettingsStore.backAction.asState()
    val doubleClickAction by BehaviorSettingsStore.doubleClickAction.asState()
    val homeAction by BehaviorSettingsStore.homeAction.asState()
    val leftPadding by BehaviorSettingsStore.leftPadding.asState()
    val rightPadding by BehaviorSettingsStore.rightPadding.asState()
    val topPadding by BehaviorSettingsStore.topPadding.asState()
    val bottomPadding by BehaviorSettingsStore.bottomPadding.asState()

    val lockMethod by PrivateSettingsStore.lockMethod.asState()
    val superWarningModeEnabled = lockMethod != LockMethod.NONE

    val paddingState = rememberExpandableSection(stringResource(R.string.drag_zone_padding))

    val showAppPreviewOverlay = paddingState.isExpanded()


    // Lock settings state
    val currentLockMethod by PrivateSettingsStore.lockMethod.asState()
    val pinHash by PrivateSettingsStore.lockPinHash.asState()

    var showLockMethodPicker by remember { mutableStateOf(false) }
    var showPinSetupDialog by remember { mutableStateOf(false) }
    var showRemovePinConfirm by remember { mutableStateOf(false) }
    var pendingLockMethod by remember { mutableStateOf<LockMethod?>(null) }

    val superWarningState = rememberExpandableSection(stringResource(R.string.super_warning_mode)
    ) { currentLockMethod != LockMethod.NONE }

    val forceAppLanguageSelector by DebugSettingsStore.forceAppLanguageSelector.asState()


    SettingsLazyHeader(
        title = stringResource(R.string.behavior),
        onBack = onBack,
        helpText = stringResource(R.string.behavior_help),
        onReset = {
            scope.launch {
                UiSettingsStore.resetAll(ctx)
            }
        }
    ) {
        item {
            DragonColumnGroup {
                SettingsSwitchRow(
                    setting = BehaviorSettingsStore.keepScreenOn,
                    title = stringResource(R.string.keep_screen_on),
                    description = stringResource(R.string.keep_screen_on_desc)
                )

                SettingsSwitchRow(
                    setting = BehaviorSettingsStore.disableHapticFeedbackGlobally,
                    title = stringResource(R.string.disable_haptic_globally),
                    description = stringResource(R.string.disable_haptic_globally_desc)
                )

                SettingsSwitchRow(
                    setting = BehaviorSettingsStore.pointsActionSnapsToOuterCircle,
                    title = stringResource(R.string.point_action_snaps_to_outer_circle),
                    description = stringResource(R.string.point_action_snaps_to_outer_circle_desc)
                )

                SettingsSwitchRow(
                    setting = BehaviorSettingsStore.promptForShortcutsWhenAddingApp,
                    title = stringResource(R.string.prompt_shortcuts_when_adding_app),
                    description = stringResource(R.string.prompt_shortcuts_when_adding_app_desc)
                )

                SettingsSwitchRow(
                    setting = BehaviorSettingsStore.useDifferentialLoadingForPrivateSpace,
                    title = stringResource(R.string.use_differential_loading_private_space),
                    description = stringResource(R.string.use_differential_loading_private_space_desc)
                ) {
                    if (it) {
                        scope.launch {
                            ctx.showToast("Reloading apps")
                            appLifecycleViewModel.onUnlockPrivateSpace()
                        }
                    } else {
                        scope.launch {
                            ctx.showToast("Removing cache")
                            PrivateAppsSettingsStore.resetAll(ctx)
                        }
                    }
                }

                SettingsSlider(
                    setting = BehaviorSettingsStore.offScreenTimeout,
                    title = stringResource(R.string.off_screen_timeout),
                    description = stringResource(R.string.off_screen_timeout_desc),
                    valueRange = -1..60
                )
            }
        }

        item {
            DragonColumnGroup {
                CustomActionSelector(
                    currentAction = backAction,
                    label = stringResource(R.string.back_action),
                    onToggle = {
                        scope.launch {
                            BehaviorSettingsStore.backAction.reset(ctx)
                        }
                    }
                ) {
                    scope.launch {
                        BehaviorSettingsStore.backAction.set(ctx, it)
                    }
                }

                CustomActionSelector(
                    currentAction = doubleClickAction,
                    label = stringResource(R.string.double_click_action),
                    onToggle = {
                        scope.launch {
                            BehaviorSettingsStore.doubleClickAction.reset(ctx)
                        }
                    }
                ) {
                    scope.launch {
                        BehaviorSettingsStore.doubleClickAction.set(ctx, it)
                    }
                }
                CustomActionSelector(
                    currentAction = homeAction,
                    label = stringResource(R.string.home_action),
                    onToggle = {
                        scope.launch {
                            BehaviorSettingsStore.homeAction.reset(ctx)
                        }
                    }
                ) {
                    scope.launch {
                        BehaviorSettingsStore.homeAction.set(ctx, it)
                    }
                }
            }
        }

        item {
            ExpandableSection(paddingState) {
                SliderWithLabel(
                    label = stringResource(R.string.left_padding),
                    value = leftPadding,
                    valueRange = 0..300,
                    color = MaterialTheme.colorScheme.primary,
                    showValue = true,
                    onReset = {
                        scope.launch {
                            BehaviorSettingsStore.leftPadding.reset(ctx)
                        }
                    },
                    onChange = {
                        scope.launch {
                            BehaviorSettingsStore.leftPadding.set(ctx, it)
                        }
                    }
                )

                SliderWithLabel(
                    label = stringResource(R.string.right_padding),
                    value = rightPadding,
                    valueRange = 0..300,
                    color = MaterialTheme.colorScheme.primary,
                    showValue = true,
                    onReset = {
                        scope.launch {
                            BehaviorSettingsStore.rightPadding.reset(ctx)
                        }
                    },
                    onChange = {
                        scope.launch {
                            BehaviorSettingsStore.rightPadding.set(ctx, it)
                        }
                    }
                )

                SliderWithLabel(
                    label = stringResource(R.string.top_padding),
                    value = topPadding,
                    valueRange = 0..300,
                    color = MaterialTheme.colorScheme.primary,
                    showValue = true,
                    onReset = {
                        scope.launch {
                            BehaviorSettingsStore.topPadding.reset(ctx)
                        }
                    },
                    onChange = {
                        scope.launch {
                            BehaviorSettingsStore.topPadding.set(ctx, it)
                        }
                    }
                )

                SliderWithLabel(
                    label = stringResource(R.string.bottom_padding),
                    value = bottomPadding,
                    valueRange = 0..300,
                    color = MaterialTheme.colorScheme.primary,
                    showValue = true,
                    onReset = {
                        scope.launch {
                            BehaviorSettingsStore.bottomPadding.reset(ctx)
                        }
                    },
                    onChange = {
                        scope.launch {
                            BehaviorSettingsStore.bottomPadding.set(ctx, it)
                        }
                    }
                )
            }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.settings_language_title),
                icon = Icons.Default.Language,
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !forceAppLanguageSelector) {
                        openSystemLanguageSettings(ctx)
                    } else {
                        navController.navigate(SETTINGS.LANGUAGE)
                    }
                }
            )
        }

        item {
            val lockDescription = when (currentLockMethod) {
                LockMethod.NONE -> stringResource(R.string.lock_none)
                LockMethod.PIN -> stringResource(R.string.lock_pin)
                LockMethod.DEVICE_UNLOCK -> stringResource(R.string.lock_device_unlock)
            }
            SettingsItem(
                title = stringResource(R.string.lock_method),
                description = lockDescription,
                icon = Icons.Default.Lock
            ) {
                showLockMethodPicker = true
            }
        }

        if (currentLockMethod == LockMethod.PIN) {
            item {
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Max)
                        .animateItem(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    SettingsItem(
                        title = stringResource(R.string.change_pin),
                        icon = Icons.Default.Fingerprint,
                        modifier = Modifier.weight(1f)
                    ) {
                        showPinSetupDialog = true
                    }

                    SettingsItem(
                        title = stringResource(R.string.remove_pin),
                        icon = Icons.Default.Close,
                        modifier = Modifier.weight(1f)
                    ) {
                        showRemovePinConfirm = true
                    }
                }
            }
        }

        item {
            ExpandableSection(superWarningState) {
                SettingsSwitchRow(
                    setting = BehaviorSettingsStore.superWarningMode,
                    enabled = superWarningModeEnabled,
                    title = stringResource(R.string.super_warning_mode),
                    description = stringResource(R.string.super_warning_mode_desc),
                )

                SettingsSwitchRow(
                    setting = BehaviorSettingsStore.vibrateOnError,
                    enabled = superWarningModeEnabled,
                    title = stringResource(R.string.vibrate_on_error),
                    description = stringResource(R.string.vibrate_on_error_desc),
                )

                SettingsSwitchRow(
                    setting = BehaviorSettingsStore.alarmSound,
                    enabled = superWarningModeEnabled,
                    title = stringResource(R.string.alarm_sound),
                    description = stringResource(R.string.super_warning_mode_desc),
                )

                SettingsSwitchRow(
                    setting = BehaviorSettingsStore.metalPipesSound,
                    enabled = superWarningModeEnabled,
                    title = stringResource(R.string.metal_pipes_sound),
                    description = stringResource(R.string.metal_pipes_sound_desc),
                )

                SettingsSlider(
                    setting = BehaviorSettingsStore.superWarningModeSound,
                    enabled = superWarningModeEnabled,
                    title = stringResource(R.string.super_warning_mode_sound),
                    description = stringResource(R.string.super_warning_mode_sound_desc),
                    valueRange = 0..100
                )
            }
        }
    }


    if (showAppPreviewOverlay) {
        Canvas(Modifier.fillMaxSize()) {
            drawRect(
                color = Color(0x55FF0000),
                topLeft = Offset(
                    leftPadding.toFloat(),
                    topPadding.toFloat()
                ),
                size = Size(
                    size.width - leftPadding - rightPadding.toFloat(),
                    size.height - topPadding - bottomPadding.toFloat()
                )
            )
        }
    }

    // ── PIN setup dialog ──
    if (showPinSetupDialog) {
        PinSetupDialog(
            onDismiss = {
                showPinSetupDialog = false
                pendingLockMethod = null
            },
            onPinSet = { pin ->
                scope.launch {
                    val hash = SecurityHelper.hashPin(pin)
                    PrivateSettingsStore.lockPinHash.set(ctx, hash)
                    PrivateSettingsStore.lockMethod.set(ctx, LockMethod.PIN)
                    ctx.showToast(ctx.getString(R.string.pin_set_success))
                }
                showPinSetupDialog = false
                pendingLockMethod = null
            }
        )
    }

    if (showRemovePinConfirm) {
        var pin by remember { mutableStateOf("") }
        val pinShapes = remember { mutableStateListOf<IconShape>() }
        var failedTries by remember { mutableIntStateOf(0) }

        PinUnlockDialog(
            onDismiss = { showRemovePinConfirm = false },
            onValidate = {
                if (SecurityHelper.verifyPin(pin, pinHash)) {
                    scope.launch {
                        PrivateSettingsStore.lockMethod.reset(ctx)
                        showRemovePinConfirm = false
                    }
                } else {
                    ctx.showToast(ctx.getString(R.string.wrong_pin))
                    pin = ""
                    pinShapes.clear()
                    failedTries++
                }
            },
            pin = { pin },
            pinShapes = { pinShapes },
            failedTries = { failedTries },
            onPinChanged = { newValue ->
                pin = newValue
                if (pinShapes.size < newValue.length) {
                    repeat(newValue.length - pinShapes.size) {
                        pinShapes.add(allShapesWithoutRandom.random())
                    }
                } else {
                    repeat(pinShapes.size - newValue.length) {
                        pinShapes.removeAt(pinShapes.lastIndex)
                    }
                }
            }
        )
    }


    // ── Lock method picker dialog ──
    if (showLockMethodPicker) {
        val methods = LockMethod.entries
        val methodLabels = methods.map { method ->
            when (method) {
                LockMethod.NONE -> stringResource(R.string.lock_none)
                LockMethod.PIN -> stringResource(R.string.lock_pin)
                LockMethod.DEVICE_UNLOCK -> stringResource(R.string.lock_device_unlock)
            }
        }

        CustomAlertDialog(
            onDismissRequest = { showLockMethodPicker = false },
            title = {
                Text(
                    stringResource(R.string.lock_method),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.lock_settings_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                    )
                    Spacer(Modifier.height(8.dp))
                    methods.forEachIndexed { index, method ->
                        val isAvailable = when (method) {
                            LockMethod.DEVICE_UNLOCK -> SecurityHelper.isDeviceUnlockAvailable(ctx)
                            else -> true
                        }
                        val unavailableText = when (method) {
                            LockMethod.DEVICE_UNLOCK -> if (!isAvailable) stringResource(R.string.device_credentials_not_available) else null
                            else -> null
                        }
                        SettingsItem(
                            title = methodLabels[index],
                            description = unavailableText,
                            enabled = isAvailable,
                            backgroundColor = if (method == currentLockMethod)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface
                        ) {
                            when (method) {
                                LockMethod.PIN -> {
                                    pendingLockMethod = LockMethod.PIN
                                    showLockMethodPicker = false
                                    showPinSetupDialog = true
                                }

                                LockMethod.NONE -> {
                                    if (currentLockMethod == LockMethod.PIN) {
                                        // Remove PIN
                                        scope.launch {
                                            PrivateSettingsStore.lockPinHash.set(ctx, "")
                                            PrivateSettingsStore.lockMethod.set(
                                                ctx,
                                                LockMethod.NONE
                                            )
                                            ctx.showToast(ctx.getString(R.string.pin_removed))
                                        }
                                    } else {
                                        scope.launch {
                                            PrivateSettingsStore.lockMethod.set(
                                                ctx,
                                                LockMethod.NONE
                                            )
                                        }
                                    }
                                    showLockMethodPicker = false
                                }

                                LockMethod.DEVICE_UNLOCK -> {
                                    // Test biometric authentication immediately
                                    val activity = ctx.findFragmentActivity()
                                    if (activity != null && SecurityHelper.isDeviceUnlockAvailable(
                                            ctx
                                        )
                                    ) {
                                        SecurityHelper.showDeviceUnlockPrompt(
                                            activity = activity,
                                            onSuccess = {
                                                scope.launch {
                                                    PrivateSettingsStore.lockMethod.set(
                                                        ctx,
                                                        method
                                                    )
                                                }
                                                showLockMethodPicker = false
                                            },
                                            onError = { msg ->
                                                ctx.showToast(
                                                    ctx.getString(
                                                        R.string.authentication_error,
                                                        msg
                                                    )
                                                )
                                            },
                                            onFailed = {
                                                ctx.showToast(ctx.getString(R.string.authentication_failed))
                                            }
                                        )
                                    } else {
                                        ctx.showToast(ctx.getString(R.string.device_credentials_not_available))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun openSystemLanguageSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}
