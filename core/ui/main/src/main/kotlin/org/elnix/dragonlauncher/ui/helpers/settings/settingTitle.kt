package org.elnix.dragonlauncher.ui.helpers.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.ui.components.dragon.DragonIconButton


@Composable
fun SettingsTitle(
    title: String,
    vararg otherIcons: Triple<(() -> Unit), ImageVector, String>,
    resetIcon: (() -> Unit)?,
    helpIcon: () -> Unit,
    onBack: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        DragonIconButton(
            onClick = onBack,
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.back)
        )

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            otherIcons.forEach {
                DragonIconButton(
                    onClick = { it.first() },
                    imageVector = it.second,
                    contentDescription = it.third
                )
            }

            if (resetIcon != null) {
                DragonIconButton(
                    onClick = { resetIcon() },
                    imageVector = Icons.Default.Restore,
                    contentDescription = stringResource(R.string.reset)
                )
            }

            DragonIconButton(
                onClick = { helpIcon() },
                imageVector = Icons.AutoMirrored.Filled.Help,
                contentDescription = stringResource(R.string.help)
            )
        }
    }
}
