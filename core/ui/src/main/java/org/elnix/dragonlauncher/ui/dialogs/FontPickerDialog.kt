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
import org.elnix.dragonlauncher.common.utils.showToast
import android.content.Intent
import android.util.Log
import android.content.pm.PackageManager
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
                                    // If font file exists locally, apply it. Otherwise request from extensions.
                                    val extDir = File(ctx.getExternalFilesDir(null), "fonts")
                                    val ttf = File(extDir, "$font.ttf")
                                    val otf = File(extDir, "$font.otf")

                                    if (ttf.exists() || otf.exists()) {
                                        UiSettingsStore.globalFont.set(ctx, font)
                                    } else {
                                        // Not present locally: try to request from the specific fonts extension (Additional Fonts)
                                        try {
                                            val pm = ctx.packageManager
                                            val fontsExtPkg = "org.dragon.launcher.fonts"
                                            
                                            val isInstalled = try {
                                                pm.getPackageInfo(fontsExtPkg, 0)
                                                true
                                            } catch (e: Exception) { false }

                                            if (isInstalled) {
                                                val i = Intent("org.dragon.launcher.ACTION_GET_FONTS").apply {
                                                    putExtra("FONT_NAME", font)
                                                    setPackage(fontsExtPkg)
                                                }
                                                // FontProviderService uses onStartCommand, we trigger it via startService
                                                ctx.startService(i)
                                                Log.d("FontPicker", "Requested font $font from $fontsExtPkg")
                                                ctx.showToast("Downloading $font from Additional Fonts...")
                                            } else {
                                                ctx.showToast("Additional Fonts extension not found")
                                                Log.d("FontPicker", "Candidate extension $fontsExtPkg not installed")
                                            }
                                        } catch (e: Exception) {
                                            Log.e("FontPicker", "Request error: ${e.message}")
                                            ctx.showToast("Error requesting font")
                                        }
                                    }
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
