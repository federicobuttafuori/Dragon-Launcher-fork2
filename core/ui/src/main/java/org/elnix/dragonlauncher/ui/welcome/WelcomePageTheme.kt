package org.elnix.dragonlauncher.ui.welcome

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
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

@Composable
fun WelcomePageTheme() {
    val ctx = LocalContext.current

    var themes by remember { mutableStateOf<List<ThemeObject>?>(null) }

    LaunchedEffect(Unit) {
        themes = loadThemes(ctx)
    }

    WelcomePagerHeader(
        title = stringResource(R.string.theme_selector),
        icon = Icons.Default.ColorLens
    ) {
        ThemesList(themes)
    }
}
