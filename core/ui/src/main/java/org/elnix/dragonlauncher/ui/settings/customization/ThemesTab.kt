package org.elnix.dragonlauncher.ui.settings.customization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.utils.ThemeObject
import org.elnix.dragonlauncher.common.utils.loadThemes
import org.elnix.dragonlauncher.ui.helpers.ThemesList
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader

@Composable
fun ThemesTab(
    onBack: () -> Unit
) {
    val ctx = LocalContext.current

    var themes by remember { mutableStateOf<List<ThemeObject>?>(null) }

    LaunchedEffect(Unit) {
        themes = loadThemes(ctx)
    }

    SettingsLazyHeader(
        title = stringResource(R.string.theme_selector),
        onBack = onBack,
        helpText = stringResource(R.string.theme_selector_help),
        onReset = null,
        content = {
            ThemesList(themes)
        }
    )
}
