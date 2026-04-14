package org.elnix.dragonlauncher.ui.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.common.utils.resolveShape
import org.elnix.dragonlauncher.ui.actions.appIcon
import org.elnix.dragonlauncher.ui.base.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.base.modifiers.conditional
import org.elnix.dragonlauncher.ui.composition.LocalIconShape
import org.elnix.dragonlauncher.ui.dragon.components.DragonDropDownMenu

@Composable
fun AppItemHorizontal(
    app: AppModel,
    selected: Boolean = false,
    showIcons: Boolean,
    showLabels: Boolean,
    txtColor: Color,
    onLongClick: ((AppModel) -> Unit)?,
    longPressPopup: @Composable ((AppModel) -> Unit)?,
    onClick: ((AppModel) -> Unit)?
) {

    require(!((onLongClick != null) and (longPressPopup != null))) {
        "Long press action, or popup, or neither, but not both!"
    }

    val iconShape = LocalIconShape.current

    var showLongPressPopup by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(DragonShape)
                .conditional(selected) {
                    background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                }
                .combinedClickable(
                    onLongClick = {
                        if (longPressPopup != null) showLongPressPopup = true
                        else onLongClick?.invoke(app)
                    },
                    onClick = { onClick?.invoke(app) }
                )
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                if (showIcons) {
                    Image(
                        painter = appIcon(app),
                        contentDescription = app.name,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(iconShape.resolveShape()),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.width(12.dp))
                }
                if (selected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = 4.dp, y = 4.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                    )
                }
            }

            if (showLabels) {
                Text(
                    text = app.name,
                    color = txtColor
                )
            }
        }

        DragonDropDownMenu(
            expanded = showLongPressPopup,
            onDismissRequest = { showLongPressPopup = false }
        ) {
            longPressPopup!!(app)
        }
    }
}

@Composable
fun AppItemGrid(
    app: AppModel,
    selected: Boolean = false,
    showIcons: Boolean,
    maxIconSize: Int,
    showLabels: Boolean,
    txtColor: Color,
    onLongClick: ((AppModel) -> Unit)?,
    longPressPopup: @Composable ((AppModel) -> Unit)?,
    onClick: ((AppModel) -> Unit)?
) {
    require(!((onLongClick != null) and (longPressPopup != null))) {
        "Long press action, or popup, or neither, but not both!"
    }

    val iconShape = LocalIconShape.current

    var showLongPressPopup by remember { mutableStateOf(false) }

    Box {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(DragonShape)
                .conditional(selected) {
                    background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.primary,
                            DragonShape
                        )
                }
                .combinedClickable(
                    onLongClick = {
                        if (longPressPopup != null) showLongPressPopup = true
                        else onLongClick?.invoke(app)
                    },
                    onClick = { onClick?.invoke(app) }
                )
                .padding(5.dp)
        ) {
            Box {
                if (showIcons) {
                    Image(
                        painter = appIcon(app),
                        contentDescription = app.name,
                        modifier = Modifier
                            .sizeIn(maxWidth = maxIconSize.dp)
                            .aspectRatio(1f)
                            .clip(iconShape.resolveShape()),
                        contentScale = ContentScale.Fit
                    )
                }

                if (selected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = 4.dp, y = 4.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                    )
                }
            }

            if (showLabels) {
                Spacer(Modifier.height(6.dp))

                Text(
                    text = app.name,
                    color = txtColor,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        DragonDropDownMenu(
            expanded = showLongPressPopup,
            onDismissRequest = { showLongPressPopup = false }
        ) {
            longPressPopup!!(app)
        }
    }
}
