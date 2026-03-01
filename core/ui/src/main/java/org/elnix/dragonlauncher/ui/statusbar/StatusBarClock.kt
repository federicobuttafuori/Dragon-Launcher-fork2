package org.elnix.dragonlauncher.ui.statusbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import org.elnix.dragonlauncher.common.serializables.StatusBarSerializable
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.utils.openAlarmApp
import org.elnix.dragonlauncher.common.utils.openCalendar
import org.elnix.dragonlauncher.ui.modifiers.conditional
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun StatusBarDate(
    element: StatusBarSerializable.Date,
    onAction: ((SwipeActionSerializable) -> Unit)?
) {
    val ctx = LocalContext.current

    val action = element.action
    val formatter = element.formatter

    val dateFormat = remember(formatter) {
        try {
            DateTimeFormatter.ofPattern(formatter)
        } catch (e: Exception) {
            println("⚠️ Invalid date format '$formatter': ${e.message}")
            DateTimeFormatter.ofPattern("MMM dd")
        }
    }

    var date by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(formatter) {
        while (true) {
            date = LocalDate.now()
            delay(60_000L)
        }
    }

    val dateText = try {
        date.format(dateFormat)
    } catch (e: Exception) {
        println("⚠️ Date formatting failed: ${e.message}")
        date.format(DateTimeFormatter.ofPattern("MMM dd"))
    }

    Row {
        Text(
            text = dateText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.conditional(onAction != null) {
                clickable {
                    action?.let { onAction!!(it) } ?: openCalendar(ctx)
                }
            }
        )
    }
}


@Composable
fun StatusBarTime(
    element: StatusBarSerializable.Time,
    onAction: ((SwipeActionSerializable) -> Unit)?
) {
    val ctx = LocalContext.current

    val action = element.action
    val formatter = element.formatter

    val timeFormat = remember(formatter) {
        try {
            DateTimeFormatter.ofPattern(formatter)
        } catch (e: Exception) {
            println("⚠️ Invalid time format '$formatter': ${e.message}")
            DateTimeFormatter.ofPattern("HH:mm:ss")
        }
    }

    var time by remember { mutableStateOf(LocalTime.now()) }

    // Update every second if formatter contains 'ss', else every minute
    val updateInterval = if ("ss" in formatter) 1_000L else 60_000L

    LaunchedEffect(formatter) {
        while (true) {
            time = LocalTime.now()
            delay(updateInterval)
        }
    }

    val timeText = try {
        time.format(timeFormat)
    } catch (e: Exception) {
        println("⚠️ Time formatting failed: ${e.message}")
        time.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    Row {
        Text(
            text = timeText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.conditional(onAction != null) {
                clickable {
                    action?.let { onAction!!(it) } ?: openAlarmApp(ctx)
                }
            }
        )
    }
}
