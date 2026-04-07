package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.common.serializables.allShapes
import org.elnix.dragonlauncher.ui.helpers.ShapePreview

@Composable
fun ShapePickerDialog(
    selected: IconShape,
    onDismiss: () -> Unit,
    onPicked: (IconShape) -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(allShapes) { shape ->
                    ShapePreview(
                        iconShape = shape,
                        modifier = Modifier.size(60.dp),
                        selected = shape == selected
                    ) {
                        onPicked(shape)
                    }
                }
            }
        },
        confirmButton = {},
        containerColor = MaterialTheme.colorScheme.surface
    )
}
