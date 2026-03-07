package org.elnix.dragonlauncher.ui.widgets

import android.content.Context
import android.os.BatteryManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.layout.RowScope
import androidx.glance.layout.Spacer
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import org.elnix.dragonlauncher.common.serializables.StatusBarSerializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun RowScope.GlanceStatusBarItem(
    element: StatusBarSerializable,
    modifier: GlanceModifier = GlanceModifier
) {
    when (element) {
        is StatusBarSerializable.Time -> GlanceTimeItem(element, modifier)
        is StatusBarSerializable.Date -> GlanceDateItem(element, modifier)
        is StatusBarSerializable.Battery -> GlanceBatteryItem(element, modifier)
        is StatusBarSerializable.Spacer -> {
            if (element.width == -1) {
                Spacer(modifier = GlanceModifier.defaultWeight())
            } else {
                val w = if (element.width < 0) 1 else element.width
                Spacer(modifier = GlanceModifier.width(w.dp))
            }
        }
        else -> {
            // Placeholder for types not yet specifically Glance-implemented
            Text(
                text = "[${element::class.simpleName}]",
                style = TextStyle(color = GlanceTheme.colors.onSurface),
                modifier = modifier
            )
        }
    }
}

@Composable
private fun GlanceTimeItem(element: StatusBarSerializable.Time, modifier: GlanceModifier) {
    val pattern = element.formatter.ifEmpty { "HH:mm" }
    val timeStr = try {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern))
    } catch (_: Exception) {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    }
    Text(text = timeStr, style = TextStyle(color = GlanceTheme.colors.onSurface), modifier = modifier)
}

@Composable
private fun GlanceDateItem(element: StatusBarSerializable.Date, modifier: GlanceModifier) {
    val pattern = element.formatter.ifEmpty { "MMM dd" }
    val dateStr = try {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern))
    } catch (_: Exception) {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd"))
    }
    Text(text = dateStr, style = TextStyle(color = GlanceTheme.colors.onSurface), modifier = modifier)
}

@Composable
private fun GlanceBatteryItem(element: StatusBarSerializable.Battery, modifier: GlanceModifier) {
    val context = LocalContext.current
    val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    val level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

    if (element.showPercentage) {
        Text(text = "$level%", style = TextStyle(color = GlanceTheme.colors.onSurface), modifier = modifier)
    }
}
