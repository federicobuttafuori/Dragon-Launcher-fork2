package org.elnix.dragonlauncher.ui.helpers

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.utils.ThemeObject
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.SettingsBackupManager
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.dialogs.ThemeJsonPopup
import org.json.JSONObject

@Composable
fun ThemesList(themes: List<ThemeObject>?) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var showJson by remember { mutableStateOf<JSONObject?>(null) }

    if (themes == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.loading_themes))
            Spacer(Modifier.height(20.dp))
            CircularProgressIndicator()
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(themes) { theme ->
                Card(
                    colors = AppObjectsColors.cardColors(),
                    modifier = Modifier
                        .combinedClickable(
                            onLongClick = { showJson = theme.json },
                            onClick = {
                                scope.launch {
                                    SettingsBackupManager.importSettingsFromJson(
                                        ctx,
                                        theme.json,
                                        DataStoreName.entries.toSet()
                                    )
                                }
                            }
                        )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = theme.name,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )

                        Image(
                            painter = if (theme.imageAssetPath != null) {
                                rememberAssetPainter(theme.imageAssetPath!!)
                            } else {
                                painterResource(R.drawable.ic_app_default)
                            },
                            contentDescription = theme.name,
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }

    if (showJson != null) {
        ThemeJsonPopup(showJson!!) { showJson = null }
    }
}


@Composable
private fun rememberAssetPainter(assetPath: String): Painter {
    val ctx = LocalContext.current
    val bitmap = remember(assetPath) {
        ctx.assets.open(assetPath).use { BitmapFactory.decodeStream(it) }
    }
    return BitmapPainter(bitmap.asImageBitmap())
}
