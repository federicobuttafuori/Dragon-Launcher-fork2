package org.elnix.dragonlauncher.ui.helpers.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.utils.colors.ColorUtils.semiTransparentIfDisabled
import org.elnix.dragonlauncher.ui.components.dragon.DragonIconButton
import org.elnix.dragonlauncher.ui.components.dragon.DragonRow
import org.elnix.dragonlauncher.ui.helpers.text.TextWithDescription

@Composable
fun SettingsItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    leadIcon: ImageVector? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {

    DragonRow(
        modifier = modifier,
        enabled = enabled,
        onLongClick = onLongClick,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.weight(1f)
        ) {

            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.semiTransparentIfDisabled(enabled)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                TextWithDescription(
                    text = title,
                    description = description
                )
            }
            if (leadIcon != null) {
                Icon(
                    imageVector = leadIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.semiTransparentIfDisabled(enabled),
                    modifier = Modifier.sizeIn(maxHeight = 25.dp)
                )
            }
        }
    }
}


@Composable
fun SettingItemWithExternalOpen(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    leadIcon: ImageVector? = null,
    extIcon: ImageVector = Icons.AutoMirrored.Filled.OpenInNew,
    onLongClick: (() -> Unit)? = null,
    onExtClick: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.height(IntrinsicSize.Min)
    ) {
        SettingsItem(
            title = title,
            modifier = Modifier.weight(1f),
            description = description,
            enabled = enabled,
            icon = icon,
            leadIcon = leadIcon,
            onLongClick = onLongClick,
            onClick = onClick
        )

        DragonIconButton(
            onClick = onExtClick,
            contentDescription = title,
            imageVector = extIcon,
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 56.dp),
        )
    }
}
