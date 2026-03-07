package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.common.utils.Constants.Logging.SHAPES_TAG
import org.elnix.dragonlauncher.common.utils.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.components.dragon.DragonIconButton


@Composable
fun ShapeRow(
    selected: IconShape,
    title: String = stringResource(R.string.edit_icons_shape),
    onReset:() -> Unit,
    onClick: () -> Unit
) {

    val ctx = LocalContext.current

    ctx.logD(SHAPES_TAG) { "Selected: $selected" }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DragonShape)
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.weight(1f)
        ) {

            ShapePreview(selected)
            logD(SHAPES_TAG) { "Current Shape: $selected" }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = stringResource(R.string.edit_icons_shape_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.sizeIn(maxHeight = 30.dp)
                )
            }
        }

        DragonIconButton(
            onClick = onReset,
            colors = AppObjectsColors.iconButtonColors()
        ) {
            Icon(
                imageVector = Icons.Default.Restore,
                contentDescription = stringResource(R.string.reset),
            )
        }
    }
}
