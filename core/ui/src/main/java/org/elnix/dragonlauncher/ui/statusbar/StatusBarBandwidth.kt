package org.elnix.dragonlauncher.ui.statusbar

import android.annotation.SuppressLint
import android.net.TrafficStats
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.elnix.dragonlauncher.common.serializables.StatusBarSerializable

@Composable
fun StatusBarBandwidth(
    element: StatusBarSerializable.Bandwidth,
    modifier: Modifier = Modifier
) {
    var rxSpeed by remember { mutableLongStateOf(0L) }
    var txSpeed by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        var prevRx = TrafficStats.getTotalRxBytes()
        var prevTx = TrafficStats.getTotalTxBytes()
        while (isActive) {
            delay(1_000L)
            val currentRx = TrafficStats.getTotalRxBytes()
            val currentTx = TrafficStats.getTotalTxBytes()
            rxSpeed = if (currentRx >= 0 && prevRx >= 0) currentRx - prevRx else 0L
            txSpeed = if (currentTx >= 0 && prevTx >= 0) currentTx - prevTx else 0L
            prevRx = currentRx
            prevTx = currentTx
        }
    }

    val displayText = if (element.merge) {
        formatSpeed(rxSpeed + txSpeed)
    } else {
        "↓${formatSpeed(rxSpeed)} ↑${formatSpeed(txSpeed)}"
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = displayText,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@SuppressLint("DefaultLocale")
private fun formatSpeed(bytesPerSecond: Long): String {
    return when {
        bytesPerSecond >= 1_048_576L -> String.format("%.1fM", bytesPerSecond / 1_048_576.0)
        bytesPerSecond >= 1_024L -> String.format("%.0fK", bytesPerSecond / 1_024.0)
        else -> "${bytesPerSecond}B"
    }
}
