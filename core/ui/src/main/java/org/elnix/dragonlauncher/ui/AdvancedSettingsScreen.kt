package org.elnix.dragonlauncher.ui


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.system.Os.kill
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.utils.Constants.Links.discordInviteLink
import org.elnix.dragonlauncher.common.utils.SETTINGS
import org.elnix.dragonlauncher.common.utils.UiConstants.DragonShape
import org.elnix.dragonlauncher.common.utils.alphaMultiplier
import org.elnix.dragonlauncher.common.utils.copyToClipboard
import org.elnix.dragonlauncher.common.utils.getVersionCode
import org.elnix.dragonlauncher.common.utils.obtainiumPackageName
import org.elnix.dragonlauncher.common.utils.openUrl
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.settings.SettingsStoreRegistry
import org.elnix.dragonlauncher.settings.stores.DebugSettingsStore
import org.elnix.dragonlauncher.settings.stores.PrivateSettingsStore
import org.elnix.dragonlauncher.ui.components.TextDivider
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.helpers.settings.ContributorItem
import org.elnix.dragonlauncher.ui.helpers.settings.SettingItemWithExternalOpen
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsItem
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.ui.remembers.LocalAppsViewModel


@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun AdvancedSettingsScreen(
    navController: NavController,
    onLaunchAction: (SwipeActionSerializable) -> Unit,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val appsViewModel = LocalAppsViewModel.current

    val scope = rememberCoroutineScope()

    val versionCode = getVersionCode(ctx)

    val isDebugModeEnabled by DebugSettingsStore.debugEnabled.asState()
    val forceAppLanguageSelector by DebugSettingsStore.forceAppLanguageSelector.asState()


    val allApps by appsViewModel.allApps.collectAsState()
    val isObtainiumInstalled = allApps.filter { it.packageName == obtainiumPackageName }.size == 1

    var toast by remember { mutableStateOf<Toast?>(null) }
    val versionName = ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName ?: "unknown"
    var timesClickedOnVersion by remember { mutableIntStateOf(0) }

    val backgroundColor = MaterialTheme.colorScheme.background

    SettingsLazyHeader(
        title = stringResource(R.string.settings),
        onBack = onBack,
        helpText = stringResource(R.string.settings),
        resetTitle = stringResource(R.string.reset_all_settings),
        resetText = stringResource(R.string.every_setting_will_return_to_its_default_state_this_cannot_be_undone_the_app_will_kill_itself),
        onReset = {
            scope.launch {
                // Reset all stores, one by one, using their defined resetAll functions
                SettingsStoreRegistry.byName.entries.filter {
                    it.value != PrivateSettingsStore
                }.forEach {
                    it.value.resetAll(ctx)
                }

                // Small delay to allow the default apps to load before initializing
                delay(200)
                PrivateSettingsStore.resetAll(ctx)

                /* Kill App to also reset viewModels and caches */
                kill(9, 9)
            }
        },
    ) {
        item {
            SettingsItem(
                title = stringResource(R.string.appearance),
                icon = Icons.Default.ColorLens
            ) {
                navController.navigate(SETTINGS.APPEARANCE)
            }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.behavior),
                icon = Icons.Default.QuestionMark
            ) {
                navController.navigate(SETTINGS.BEHAVIOR)
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
            SettingsItem(
                title = stringResource(R.string.backup_restore),
                icon = Icons.Default.Restore
            ) {
                navController.navigate(SETTINGS.BACKUP)
            }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.app_drawer),
                icon = Icons.Default.GridOn
            ) {
                navController.navigate(SETTINGS.DRAWER)
            }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.workspaces),
                icon = Icons.Default.Workspaces
            ) {
                navController.navigate(SETTINGS.WORKSPACE)
            }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.widgets),
                icon = Icons.Default.Widgets
            ) {
                navController.navigate(SETTINGS.WIDGETS)
            }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.permissions),
                icon = Icons.Default.Security
            ) {
                navController.navigate(SETTINGS.PERMISSIONS)
            }
        }

        item {
            SettingItemWithExternalOpen(
                title = stringResource(R.string.extensions),
                icon = Icons.Default.Extension,
                onExtClick = { ctx.openUrl("https://github.com/Elnix90/Dragon-Launcher-Extensions") }
            ) { navController.navigate(SETTINGS.EXTENSIONS) }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.wellbeing),
                icon = Icons.Default.SelfImprovement
            ) {
                navController.navigate(SETTINGS.WELLBEING)
            }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.android_settings),
                icon = Icons.Default.SettingsSuggest,
                leadIcon = Icons.AutoMirrored.Filled.Launch
            ) {
                val packageName = ctx.packageName
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                ctx.startActivity(intent)
            }
        }


        if (isDebugModeEnabled) {
            item {
                SettingsItem(
                    title = stringResource(R.string.debug),
                    icon = Icons.Default.BugReport,
                    modifier = Modifier.animateItem()
                ) {
                    navController.navigate(SETTINGS.DEBUG)
                }
            }
        }

        item {
            SettingsItem(
                title = "Logs",
                icon = Icons.AutoMirrored.Filled.Notes,
                modifier = Modifier.animateItem()
            ) {
                navController.navigate(SETTINGS.LOGS)
            }
        }


        item { TextDivider(stringResource(R.string.about)) }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(DragonShape)
                        .clickable { ctx.openUrl("https://github.com/Elnix90/Dragon-Launcher") },
                    horizontalArrangement = Arrangement.Center
                ) {

                    val githubIcon = if (backgroundColor.luminance() < 0.5) {
                        R.drawable.github_invertocat_white
                    } else {
                        R.drawable.github_invertocat_black
                    }

                    val githubLogo = if (backgroundColor.luminance() < 0.5) {
                        R.drawable.github_logo_white
                    } else {
                        R.drawable.github_logo
                    }

                    Icon(
                        painter = painterResource(githubIcon),
                        contentDescription = "Github Icon",
                        tint = Color.Unspecified
                    )

                    Image(
                        painter = painterResource(githubLogo),
                        contentDescription = "Github Logo"
                    )
                }

                Spacer(Modifier.width(12.dp))

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(DragonShape)
                        .clickable { ctx.openUrl(discordInviteLink) },
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.discord_logo_blurple),
                        contentDescription = "Discord Logo"
                    )
                }
            }
        }

        item {

            SettingItemWithExternalOpen(
                title = stringResource(R.string.changelogs),
                icon = Icons.AutoMirrored.Filled.Notes,
                onExtClick = { ctx.openUrl("https://github.com/Elnix90/Dragon-Launcher/blob/main/fastlane/metadata/android/en-US/changelogs/${versionCode}.txt") }
            ) { navController.navigate(SETTINGS.CHANGELOGS) }

        }

        item {
            SettingsItem(
                title = stringResource(R.string.source_code),
                icon = Icons.Default.Code,
                leadIcon = Icons.AutoMirrored.Filled.Launch,
                onLongClick = { ctx.copyToClipboard("https://github.com/Elnix90/Dragon-Launcher") }
            ) { ctx.openUrl("https://github.com/Elnix90/Dragon-Launcher") }
        }

        item {
            if (isObtainiumInstalled) {
                SettingItemWithExternalOpen(
                    title = stringResource(R.string.check_for_update),
                    description = stringResource(R.string.check_for_updates_obtainium),
                    icon = Icons.Default.Update,
                    leadIcon = painterResource(R.drawable.obtainium),
                    onLongClick = { ctx.copyToClipboard("https://github.com/Elnix90/Dragon-Launcher/releases/latest") },
                    onExtClick = { ctx.openUrl("https://github.com/Elnix90/Dragon-Launcher/releases/latest") }
                ) {
                    onLaunchAction(SwipeActionSerializable.LaunchApp(obtainiumPackageName, false, 0))
                }
            } else {
                SettingsItem(
                    title = stringResource(R.string.check_for_update),
                    description = stringResource(R.string.check_for_updates_github),
                    icon = Icons.Default.Update,
                    leadIcon = Icons.AutoMirrored.Filled.Launch,
                    onLongClick = { ctx.copyToClipboard("https://github.com/Elnix90/Dragon-Launcher/releases/latest") }
                ) {
                    ctx.openUrl("https://github.com/Elnix90/Dragon-Launcher/releases/latest")
                }
            }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.report_a_bug),
                description = stringResource(R.string.open_an_issue_on_github),
                icon = Icons.Default.ReportProblem,
                leadIcon = Icons.AutoMirrored.Filled.Launch,
                onLongClick = { ctx.copyToClipboard("https://github.com/Elnix90/Dragon-Launcher/issues/new") }
            ) { ctx.openUrl("https://github.com/Elnix90/Dragon-Launcher/issues/new") }
        }


        item {
            TextDivider(
                stringResource(R.string.contributors),
                Modifier.padding(horizontal = 60.dp)
            )
        }

        item {
            ContributorItem(
                name = "Elnix90",
                imageRes = R.drawable.elnix90,
                description = stringResource(R.string.app_developer),
                githubUrl = "https://github.com/Elnix90"
            )
        }

        item {
            Row(
                modifier = Modifier
                    .padding(5.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.yoanndev90),
                    contentDescription = "YoannDev90 profile picture",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable {
                            ctx.openUrl("https://github.com/YoannDev90")
                        }
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Fit
                )
                Image(
                    painter = painterResource(R.drawable.lucky_the_cookie),
                    contentDescription = "LuckyTheCookie profile picture",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable {
                            ctx.openUrl("https://lthb.fr")
                        }
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Fit
                )
                Image(
                    painter = painterResource(R.drawable.acress1),
                    contentDescription = "Acress1 profile picture",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable {
                            ctx.openUrl("https://github.com/acress1")
                        }
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Fit
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val infoStyle = MaterialTheme.typography.labelSmall
                val infoColor = MaterialTheme.colorScheme.onBackground.alphaMultiplier(0.7f)

                val debugModeAlreadyEnabledText = stringResource(R.string.debug_mode_already_enabled)
                val deviceInfoCopiedToClipboard = stringResource(R.string.device_info_copied_to_clipboard)

                Text(
                    text = "Dragon Launcher $versionName ($versionCode)",
                    style = infoStyle,
                    textAlign = TextAlign.Center,
                    color = infoColor,
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 16.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            toast?.cancel()

                            when {

                                timesClickedOnVersion == 0 -> {
                                    ctx.copyToClipboard(versionName)
                                    ctx.showToast(deviceInfoCopiedToClipboard)
                                    timesClickedOnVersion += 1
                                }

                                isDebugModeEnabled -> {
                                    toast = Toast.makeText(
                                        ctx,
                                        debugModeAlreadyEnabledText,
                                        Toast.LENGTH_SHORT
                                    )
                                    toast?.show()
                                }


                                timesClickedOnVersion < 6 -> {
                                    timesClickedOnVersion++
                                    if (timesClickedOnVersion > 2) {
                                        toast = Toast.makeText(
                                            ctx,
                                            "${7 - timesClickedOnVersion} more times to enable Debug Mode",
                                            Toast.LENGTH_SHORT
                                        )
                                    }
                                    toast?.show()
                                }

                                else -> {
                                    scope.launch { DebugSettingsStore.debugEnabled.set(ctx, true) }
                                }
                            }
                        }
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun openSystemLanguageSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}
