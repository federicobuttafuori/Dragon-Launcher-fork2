package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import org.elnix.dragonlauncher.ui.dialogs.CustomAlertDialog
import java.io.File

@Composable
fun FontPickerDialog(onDismiss: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val globalFontName by UiSettingsStore.globalFont.asState()

    val availableFonts = remember {
        val base = listOf(
            "Default", "Serif", "SansSerif", "Monospace", "Cursive",
            "Inter", "Montserrat", "Outfit", "PoiretOne", "Quicksand",
            "Raleway", "RobotoCondensed", "SpaceGrotesk", "Urbanist"
        )
        val extFonts = try {
            val extDir = File(ctx.getExternalFilesDir(null), "fonts")
            if (extDir.exists()) {
                extDir.listFiles { file -> file.extension == "ttf" || file.extension == "otf" }
                    ?.map { it.nameWithoutExtension }
                    ?.sorted() ?: emptyList()
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        base + extFonts
    }

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
            LazyColumn {
                items(availableFonts) { font ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(DragonShape)
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

                        val displayFont = fontNameToFont(font, ctx)
                        Text(
                            text = font,
                            fontFamily = displayFont
                        )
                    }
                }
            }
        }
    )
}

fun fontNameToFont(name: String, context: android.content.Context? = null): FontFamily {
    val base = when (name) {
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
        "Default" -> FontFamily.Default
        else -> null
    }

    if (base != null) return base

    if (context != null) {
        try {
            val extDir = File(context.getExternalFilesDir(null), "fonts")
            val ttf = File(extDir, "$name.ttf")
            val otf = File(extDir, "$name.otf")
            
            val fontFile = when {
                ttf.exists() -> ttf
                otf.exists() -> otf
                else -> null
            }

            if (fontFile != null) {
                return FontFamily(Font(fontFile))
            }
        } catch (e: Exception) {
            android.util.Log.e("FontPicker", "Error loading custom font $name: ${e.message}")
        }
    }

    return FontFamily.Default
}
