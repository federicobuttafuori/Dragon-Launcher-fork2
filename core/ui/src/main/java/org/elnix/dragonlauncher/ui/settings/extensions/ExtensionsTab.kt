package org.elnix.dragonlauncher.ui.settings.extensions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.ExtensionModel
import org.elnix.dragonlauncher.common.utils.loadExtensionRegistry
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.services.ExtensionManager
import org.elnix.dragonlauncher.ui.components.ExpandableSection
import org.elnix.dragonlauncher.ui.components.dragon.DragonButton
import org.elnix.dragonlauncher.ui.components.dragon.DragonColumnGroup
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import java.util.Locale

@Composable
fun ExtensionsTab(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var extensions by remember { mutableStateOf<List<ExtensionModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val registry = loadExtensionRegistry(context)
        extensions = registry?.extensions ?: emptyList()
        isLoading = false
    }

    SettingsLazyHeader(
        title = stringResource(R.string.extensions),
        onBack = onBack,
        helpText = stringResource(R.string.extensions_description),
        onReset = null,
        resetText = null
    ) {
        if (isLoading) {
            item {
                Text(
                    text = "Loading...",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else if (extensions.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_extensions_found),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            items(extensions) { extension ->
                ExtensionItem(extension)
            }
        }

        item {
            ManualInstallSection()
        }
    }
}

@Composable
private fun ManualInstallSection() {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                // Here we would handle the APK installation from URI
                // For now, show info
                context.showToast("APK selected: ${it.path}")
            }
        }
    )

    DragonColumnGroup(
        title = stringResource(R.string.extension_manual_install_title),
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.extension_manual_install_desc),
                style = MaterialTheme.typography.bodySmall
            )
            DragonButton(
                onClick = { launcher.launch(arrayOf("application/vnd.android.package-archive")) },
                text = stringResource(R.string.select_apk)
            )
        }
    }
}

@Composable
private fun ExtensionItem(extension: ExtensionModel) {
    val context = LocalContext.current
    val currentLanguage = Locale.getDefault().language
    val description = extension.description[currentLanguage] ?: extension.description["en"] ?: ""
    
    var isInstalled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isInstalled = ExtensionManager.isExtensionInstalled(context, extension.packageName)
    }

    ExpandableSection(
        title = extension.name,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )

            if (extension.permissions.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.permissions),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
                extension.permissions.forEach { permission ->
                    Text(
                        text = "• $permission",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DragonButton(
                    onClick = { 
                        if (!isInstalled) {
                            ExtensionManager.installExtension(context, extension)
                        } else {
                            // Uninstall logic (via Intent)
                            val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .apply { data = android.net.Uri.parse("package:${extension.packageName}") }
                            context.startActivity(intent)
                        }
                    },
                    text = if (isInstalled) stringResource(R.string.uninstall) else stringResource(R.string.install),
                )
            }
        }
    }
}
