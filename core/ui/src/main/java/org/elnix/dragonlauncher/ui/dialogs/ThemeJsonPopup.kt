package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject

@Composable
fun ThemeJsonPopup(
    json: JSONObject,
    onDismiss: () -> Unit
) {
    val themeString: String = json.toString(4)

    CustomAlertDialog(
        modifier = Modifier.padding(15.dp),
        alignment = Alignment.Center,
        onDismissRequest = onDismiss,
        scroll = false,
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(themeString.lines()) { line ->
                    SelectionContainer {
                        Text(
                            text = line,
                            softWrap = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),

                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}
