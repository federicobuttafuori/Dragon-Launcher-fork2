package org.elnix.dragonlauncher
import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.serializables.StatusBarJson
import org.elnix.dragonlauncher.settings.stores.StatusBarJsonSettingsStore
import org.elnix.dragonlauncher.ui.theme.DragonLauncherTheme
import org.elnix.dragonlauncher.ui.widgets.GlanceStatusBarItem

object DragonLauncherWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                DragonWidgetPreview()
            }
        }
    }
}

@Composable
fun DragonWidgetPreview() {
    val context = androidx.glance.LocalContext.current
    val elementsJson by StatusBarJsonSettingsStore.jsonSetting.flow(context).collectAsState("")
    val elements = StatusBarJson.decodeStatusBarElements(elementsJson)

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.background)
            .clickable(actionStartActivity<MainActivity>()),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            elements.forEach { element ->
                GlanceStatusBarItem(element)
            }
        }
    }
}
