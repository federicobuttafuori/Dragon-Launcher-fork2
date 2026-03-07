package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.utils.UiConstants.DragonShape
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.components.settings.asState

@Composable
fun FontPickerDialog(onDismiss: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val globalFontName by UiSettingsStore.globalFont.asState()

    CustomAlertDialog(
        onDismissRequest = onDismiss,
        scroll = false,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        },
        title = { Text(stringResource(R.string.font_selector)) },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(
                    listOf(
                        "Default",
                        "SansSerif",
                        "Serif",
                        "Monospace",
                        "Cursive",
                        "Inter",
                        "Montserrat",
                        "Outfit",
                        "PoiretOne",
                        "Quicksand",
                        "Raleway",
                        "RobotoCondensed",
                        "SpaceGrotesk",
                        "Urbanist"
                    )
                ) { font ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(DragonShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                scope.launch {
                                    UiSettingsStore.globalFont.set(ctx, font)
                                }
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = globalFontName == font,
                            onClick = null,
                            colors = AppObjectsColors.radioButtonColors()
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = font,
                            fontFamily = fontNameToFont(font)
                        )
                    }
                }
            }
        }
    )
}


fun fontNameToFont(name: String): FontFamily = when (name) {
    "Serif" -> FontFamily.Serif
    "SansSerif" -> FontFamily.SansSerif
    "Monospace" -> FontFamily.Monospace
    "Cursive" -> FontFamily.Cursive
    "Inter" -> FontFamily(Font(R.font.inter))
    "Montserrat" -> FontFamily(Font(R.font.montserrat))
    "Outfit" -> FontFamily(Font(R.font.outfit))
    "PoiretOne" -> FontFamily(Font(R.font.poiretone))
    "Quicksand" -> FontFamily(Font(R.font.quicksand))
    "Raleway" -> FontFamily(Font(R.font.raleway))
    "RobotoCondensed" -> FontFamily(Font(R.font.robotocondensed))
    "SpaceGrotesk" -> FontFamily(Font(R.font.spacegrotesk))
    "Urbanist" -> FontFamily(Font(R.font.urbanist))
    else -> FontFamily.Default
}
