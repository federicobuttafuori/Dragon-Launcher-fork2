package org.elnix.dragonlauncher.enumsui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.utils.SETTINGS

val settingsRoutes = listOf(
    SETTINGS.ROOT,
    SETTINGS.ADVANCED_ROOT,
    SETTINGS.APPEARANCE,
    SETTINGS.WALLPAPER,
    SETTINGS.ICON_PACK,
    SETTINGS.STATUS_BAR,
    SETTINGS.THEME,
    SETTINGS.FLOATING_APPS,
    SETTINGS.COLORS,
    SETTINGS.BEHAVIOR,
    SETTINGS.DRAWER,
    SETTINGS.WORKSPACE,
    SETTINGS.BACKUP,
    SETTINGS.WELLBEING,
    SETTINGS.DEBUG,
    SETTINGS.LOGS,
    SETTINGS.SETTINGS_JSON,
    SETTINGS.LANGUAGE,
    SETTINGS.CHANGELOGS,
    SETTINGS.EXTENSIONS,
    SETTINGS.ANGLE_LINE_EDIT
)


@Composable
fun routeName(route: String): String {
    val ctx = LocalContext.current

    return when (route) {
        SETTINGS.EXTENSIONS ->
            ctx.getString(R.string.extensions)
        SETTINGS.ROOT ->
            ctx.getString(R.string.points_settings)
        SETTINGS.ADVANCED_ROOT ->
            ctx.getString(R.string.settings)
        SETTINGS.APPEARANCE ->
            ctx.getString(R.string.appearance)
        SETTINGS.WALLPAPER ->
            ctx.getString(R.string.wallpaper)
        SETTINGS.ICON_PACK ->
            ctx.getString(R.string.icon_pack)
        SETTINGS.STATUS_BAR ->
            ctx.getString(R.string.status_bar)
        SETTINGS.THEME ->
            ctx.getString(R.string.theme_selector)
        SETTINGS.FLOATING_APPS ->
            ctx.getString(R.string.widgets_floating_apps)
        SETTINGS.COLORS ->
            ctx.getString(R.string.color_selector)
        SETTINGS.BEHAVIOR ->
            ctx.getString(R.string.behavior)
        SETTINGS.DRAWER ->
            ctx.getString(R.string.app_drawer)
        SETTINGS.WORKSPACE ->
            ctx.getString(R.string.workspaces)
        SETTINGS.BACKUP ->
            ctx.getString(R.string.backup_restore)
        SETTINGS.WELLBEING ->
            ctx.getString(R.string.wellbeing)
        SETTINGS.DEBUG ->
            ctx.getString(R.string.debug)
        SETTINGS.LOGS ->
            ctx.getString(R.string.logs)
        SETTINGS.SETTINGS_JSON ->
            ctx.getString(R.string.settings_json)
        SETTINGS.LANGUAGE ->
            ctx.getString(R.string.settings_language_title)
        SETTINGS.CHANGELOGS ->
            ctx.getString(R.string.changelogs)
        SETTINGS.ANGLE_LINE_EDIT ->
            stringResource(R.string.angle_line)
        else -> ""
    }
}
