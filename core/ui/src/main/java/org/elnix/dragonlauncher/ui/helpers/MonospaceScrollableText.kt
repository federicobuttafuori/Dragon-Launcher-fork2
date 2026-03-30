package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

@Composable
fun MonospaceScrollableText(
    lines: List<String>,
    modifier: Modifier = Modifier
) {
    val horizontalScrollState = rememberScrollState()

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val density = LocalDensity.current

        val estimatedWidth = with(density) {
            (lines.maxOfOrNull { it.length } ?: 0) * 10.sp.toPx()
        }
        val minWidth = maxOf(estimatedWidth, constraints.maxWidth.toFloat())

        SelectionContainer {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScrollState)
                    .width(with(density) { minWidth.toDp() })
            ) {
                items(lines) { line ->

                    Text(
                        text = line,
                        softWrap = false,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
