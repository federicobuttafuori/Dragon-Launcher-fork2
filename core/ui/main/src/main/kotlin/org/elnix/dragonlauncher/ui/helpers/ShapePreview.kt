package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape
import org.elnix.dragonlauncher.common.utils.resolveShape
import org.elnix.dragonlauncher.ui.modifiers.conditional

@Composable
fun ShapePreview(
    iconShape: IconShape,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null
) {

    val bgColor = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.surface

    Box(
        modifier = Modifier
            .padding(5.dp)
            .then(modifier)
            .aspectRatio(1f)
            .clip(DragonShape)
            .conditional(onClick != null) {
                clickable { onClick?.invoke() }
            },
        contentAlignment = Alignment.Center
    ) {
        if (iconShape !is IconShape.Random) {
            val shape = iconShape.resolveShape()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
                    .clip(shape)
                    .background(bgColor.copy(0.5f))
                    .border(1.dp, MaterialTheme.colorScheme.secondary, shape)
            )
        } else {

            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = stringResource(R.string.random_shape),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
                    .clip(DragonShape)
                    .background(bgColor.copy(0.5f))
                    .border(1.dp, MaterialTheme.colorScheme.secondary, DragonShape)
            )
        }
    }
}
