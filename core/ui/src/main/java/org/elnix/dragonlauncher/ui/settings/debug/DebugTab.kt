@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.settings.debug

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.system.Os.kill
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.serializables.dummySwipePoint
import org.elnix.dragonlauncher.common.utils.SETTINGS
import org.elnix.dragonlauncher.common.utils.detectSystemLauncher
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.services.SystemControl
import org.elnix.dragonlauncher.services.SystemControl.activateDeviceAdmin
import org.elnix.dragonlauncher.services.SystemControl.isDeviceAdminActive
import org.elnix.dragonlauncher.settings.allStores
import org.elnix.dragonlauncher.settings.stores.DebugSettingsStore
import org.elnix.dragonlauncher.settings.stores.PrivateSettingsStore
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.components.ExpandableSection
import org.elnix.dragonlauncher.ui.components.TextDivider
import org.elnix.dragonlauncher.ui.components.dragon.DragonButton
import org.elnix.dragonlauncher.ui.components.dragon.DragonColumnGroup
import org.elnix.dragonlauncher.ui.components.settings.SettingsSwitchRow
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.dialogs.IconEditorDialog
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsItem
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.ui.remembers.LocalAppsViewModel
import org.elnix.dragonlauncher.ui.remembers.rememberExpandableSection
import org.elnix.dragonlauncher.ui.wellbeing.OverlayReminderService

@Composable
fun DebugTab(
    navController: NavController,
    onShowWelcome: () -> Unit,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val appsViewModel = LocalAppsViewModel.current

    val scope = rememberCoroutineScope()

    val systemLauncherPackageName by DebugSettingsStore.systemLauncherPackageName.asState()

    var pendingSystemLauncher by remember { mutableStateOf<String?>(null) }
    var showEditAppOverrides by remember { mutableStateOf(false) }

    val debugInfosSectionState = rememberExpandableSection(stringResource(R.string.debug_infos))
    val storeResetSectionState = rememberExpandableSection(stringResource(R.string.store_reset))


    LaunchedEffect(Unit) {
        pendingSystemLauncher = detectSystemLauncher(ctx)
    }

    SettingsLazyHeader(
        title = stringResource(R.string.debug),
        onBack = onBack,
        helpText = "Debug, too busy to make a translated explanation",
        onReset = null,
        resetText = null
    ) {

        item {
            SettingsSwitchRow(
                setting = DebugSettingsStore.debugEnabled,
                title = stringResource(R.string.activate_debug_mode),
                description = stringResource(R.string.activate_debug_mode_desc)
            ) {
                scope.launch { DebugSettingsStore.debugEnabled.set(ctx, it) }
                navController.popBackStack()
            }
        }

        item { TextDivider("Debug things") }

        item {
            SettingsItem(
                title = "Logs",
                icon = Icons.AutoMirrored.Filled.Notes
            ) {
                navController.navigate(SETTINGS.LOGS)
            }
        }

        item {
            SettingsItem(
                title = "Settings debug json",
                icon = Icons.Default.Settings
            ) {
                navController.navigate(SETTINGS.SETTINGS_JSON)
            }
        }

        item {
            DragonColumnGroup {
                DragonButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        kill(9, 9)
                    }
                ) {
                    Text("☠\uFE0F Kill app")
                }

                DragonButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        ctx.startActivity(
                            Intent(Intent.ACTION_DELETE).apply {
                                data = "package:${ctx.packageName}".toUri()
                            }
                        )
                    }
                ) {
                    Text("☠\uFE0F Uninstall app")
                }
            }
        }

        item {
            ExpandableSection(debugInfosSectionState) {
                SettingsSwitchRow(
                    setting = DebugSettingsStore.debugInfos,
                    title = stringResource(R.string.show_debug_infos),
                    description = stringResource(R.string.show_debug_infos_desc)
                )

                SettingsSwitchRow(
                    setting = DebugSettingsStore.settingsDebugInfo,
                    title = stringResource(R.string.show_debug_infos_settings),
                    description = stringResource(R.string.show_debug_infos_settings_desc)
                )

                SettingsSwitchRow(
                    setting = DebugSettingsStore.widgetsDebugInfo,
                    title = stringResource(R.string.show_debug_infos_widgets),
                    description = stringResource(R.string.show_debug_infos_widgets_desc)
                )

                SettingsSwitchRow(
                    setting = DebugSettingsStore.workspacesDebugInfo,
                    title = stringResource(R.string.show_debug_infos_workspace),
                    description = stringResource(R.string.show_debug_infos_workspace_desc)
                )

                SettingsSwitchRow(
                    setting = DebugSettingsStore.privateSpaceDebugInfo,
                    title = stringResource(R.string.private_space_debug_info),
                    description = stringResource(R.string.private_space_debug_info_desc)
                )
            }
        }

        item {
            DragonColumnGroup {
                DragonButton(
                    onClick = { onShowWelcome() },
                    colors = AppObjectsColors.buttonColors(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Show welcome screen",
                    )
                }

                DragonButton(
                    onClick = {
                        scope.launch { PrivateSettingsStore.lastSeenVersionCode.set(ctx, 0) }
                    },
                    colors = AppObjectsColors.buttonColors(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Show what's new sheet",
                    )
                }
            }
        }


        item {
            DragonColumnGroup {
                DragonButton(
                    onClick = {
                        if (!Settings.canDrawOverlays(ctx)) {
                            ctx.showToast("Overlay permission not granted")
                            return@DragonButton
                        }
                        OverlayReminderService.show(
                            ctx,
                            "TikTok",
                            "15 min",
                            "42 min",
                            "10 min",
                            true,
                            "reminder"
                        )
                    },
                    colors = AppObjectsColors.buttonColors(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Test: Reminder overlay popup")
                }

                DragonButton(
                    onClick = {
                        if (!Settings.canDrawOverlays(ctx)) {
                            ctx.showToast("Overlay permission not granted")
                            return@DragonButton
                        }
                        OverlayReminderService.show(
                            ctx,
                            "TikTok",
                            "25 min",
                            "58 min",
                            "5 min",
                            true,
                            "time_warning"
                        )
                    },
                    colors = AppObjectsColors.buttonColors(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Test: Time almost up overlay")
                }
            }
        }

        item {
            SettingsSwitchRow(
                setting = PrivateSettingsStore.hasInitialized,
                title = stringResource(R.string.has_initialized),
                description = stringResource(R.string.has_initialized_desc),
                needValidationToDisable = true
            )
        }

        item {
            SettingsSwitchRow(
                setting = PrivateSettingsStore.showSetDefaultLauncherBanner,
                title = stringResource(R.string.show_default_launcher_banner),
                description = stringResource(R.string.show_default_launcher_banner_desc)
            )
        }

        item {
            // >= Android 13
            val enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            if (!enabled) {
                Text(
                    text = "Since you're under android 13, or code name TIRAMISU you can't use the android language selector and you're blocked with the app custom one.",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            SettingsSwitchRow(
                setting = DebugSettingsStore.forceAppLanguageSelector,
                title = stringResource(R.string.force_app_language_selector),
                description = stringResource(R.string.force_app_language_selector_desc),
                enabled = enabled
            )
        }

        item {
            SettingsSwitchRow(
                setting = DebugSettingsStore.forceAppWidgetsSelector,
                title = stringResource(R.string.force_app_widgets_selector),
                description = stringResource(R.string.force_app_widgets_selector_desc)
            )
        }

//        item {
//            SettingsSwitchRow(
//                setting = DebugSettingsStore.forceAppWidgetsBinding,
//                title = "Force App Widget Binding Consent",
//                description = "Forces the system bind dialog to appear every time you add a widget (Useful for debugging/MIUI fixes)"
//            )
//        }

        item {
            SettingsSwitchRow(
                setting = DebugSettingsStore.useAccessibilityInsteadOfContextToExpandActionPanel,
                title = stringResource(R.string.use_accessibility_instead_of_context),
                description = stringResource(R.string.use_accessibility_instead_of_context_desc)
            )
        }

        item {
            ExpandableSection(storeResetSectionState) {
                allStores.entries.forEach { entry ->
                    val settingsStore = entry.value

                    OutlinedButton(
                        onClick = { scope.launch { settingsStore.resetAll(ctx) } },
                        colors = AppObjectsColors.cancelButtonColors(),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append("Reset ")
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        textDecoration = TextDecoration.Underline
                                    ),
                                ) {
                                    append(settingsStore.name)
                                }
                            }
                        )
                    }
                }
            }
        }

        item {
            TextDivider("Hacky launching accessibility things")
        }

        item {
            DragonColumnGroup {
                TextButton(
                    onClick = { SystemControl.openServiceSettings((ctx)) }
                ) {
                    Text("Open Service settings")
                }
                ActivateDeviceAdminButton()


                SettingsSwitchRow(
                    setting = DebugSettingsStore.autoRaiseDragonOnSystemLauncher,
                    title = stringResource(R.string.auto_raise_dragon_on_system_launcher),
                    description = stringResource(R.string.auto_raise_dragon_on_system_launcher_desc)
                )

                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        DragonButton(
                            onClick = {
                                pendingSystemLauncher = detectSystemLauncher(ctx)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Detect System launcher")
                        }
                        DragonButton(
                            onClick = {
                                scope.launch {
                                    DebugSettingsStore.systemLauncherPackageName.set(
                                        ctx,
                                        pendingSystemLauncher
                                    )
                                }
                            },
                            enabled = pendingSystemLauncher != null
                        ) {
                            Text("Set")
                        }
                    }

                    if (pendingSystemLauncher != null) {
                        Text(
                            buildAnnotatedString {
                                append("Your system launcher: ")
                                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                                    append(pendingSystemLauncher)
                                }
                            }
                        )
                    } else {
                        Text("No system launcher detected")
                    }
                }

                OutlinedTextField(
                    label = {
                        Text(
                            text = "Your system launcher package name",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    value = systemLauncherPackageName,
                    onValueChange = { newValue ->
                        scope.launch {
                            DebugSettingsStore.systemLauncherPackageName.set(ctx, newValue)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = AppObjectsColors.outlinedTextFieldColors()
                )
            }
        }

        item {
            TextDivider("Random debug settings")
        }

        item {
            TextButton(
                onClick = {
                    showEditAppOverrides = true
                }
            ) {
                Text(
                    text = "Edit ALL app overrides \uD83D\uDE08",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
    if (showEditAppOverrides) {
        IconEditorDialog(
            point = dummySwipePoint(),
            onDismiss = { showEditAppOverrides = false }
        ) { newIcon ->
            appsViewModel.applyIconToApps(
                icon = newIcon
            )
            showEditAppOverrides = false
        }
    }
}


@Composable
fun ActivateDeviceAdminButton() {
    val ctx = LocalContext.current
    val isActive = remember(Unit) { isDeviceAdminActive(ctx) }
    TextButton(
        enabled = !isActive,
        onClick = {
            ctx.logD("Compose", "Button clicked - context: ${ctx.packageName}")
            activateDeviceAdmin(ctx)
        }
    ) {
        Text(
            if (isActive) "Device Admin ✓ Active"
            else "Activate Device Admin"
        )
    }
}
