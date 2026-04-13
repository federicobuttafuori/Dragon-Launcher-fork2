package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import org.elnix.dragonlauncher.logging.logLevel
import org.elnix.dragonlauncher.logging.logLevelColor

@Composable
fun MonospaceScrollableText(
    lines: List<String>,
    modifier: Modifier = Modifier,
    useDragonLogsColoration: Boolean = false
) {
    val horizontalScrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxWidth()) {

        SelectionContainer {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScrollState)
            ) {
                items(lines) { line ->

                    val color = if (useDragonLogsColoration && line.length > 27 && line.startsWith("[")) {
                        val logTag = line[26]
                        logTag.logLevel.logLevelColor
                    } else null

                    Text(
                        text = line,
                        color = color ?: Color.Unspecified,
                        softWrap = false,
                        fontSize = 10.sp,
                        lineHeight = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
