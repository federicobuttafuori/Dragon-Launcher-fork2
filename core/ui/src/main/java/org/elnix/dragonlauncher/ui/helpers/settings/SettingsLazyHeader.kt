package org.elnix.dragonlauncher.ui.helpers.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.reorderable
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.ui.dialogs.UserValidation
import org.elnix.dragonlauncher.ui.modifiers.conditional

@Suppress("AssignedValueIsNeverRead")
@Composable
fun SettingsLazyHeader(
    title: String,
    onBack: () -> Unit,
    helpText: String,
    onReset: (() -> Unit)?,
    vararg otherIcons: Pair<(() -> Unit), ImageVector>,
    modifier: Modifier = Modifier,
    resetTitle: String = stringResource(R.string.reset_default_settings),
    resetText: String? = stringResource(R.string.reset_settings_in_this_tab),
    reorderState: ReorderableLazyListState? = null,
    listState: LazyListState? = null,
    titleContent: @Composable (ColumnScope.() -> Unit)? = null,
    bottomContent: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable (ColumnScope.() -> Unit)? = null,
    scrollableContent: Boolean = false,
    lazyContent: (LazyListScope.() -> Unit)? = null
) {

    var showHelpDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    requireNotNull(
        content ?: lazyContent
    ) { "Must provide exactly one of content or lazyContent, not both or neither" }


    BackHandler {
        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.exclude(WindowInsets.ime))
    ) {

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                SettingsTitle(
                    title = title,
                    otherIcons = otherIcons,
                    helpIcon = { showHelpDialog = true },
                    resetIcon = if (onReset != null) {
                        { showResetDialog = true }
                    } else null,
                ) { onBack() }


                if (titleContent != null) titleContent()
            }

            if (lazyContent != null) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(bottom = if (bottomContent != null) 0.dp else 400.dp),
                    modifier = if (reorderState != null) {
                        modifier
                            .reorderable(reorderState)
                            .detectReorderAfterLongPress(reorderState)
                    } else modifier,
                    state = reorderState?.listState ?: listState ?: rememberLazyListState()
                ) {
                    lazyContent()
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = if (reorderState != null) {
                        modifier
                            .conditional(scrollableContent) {
                                verticalScroll(rememberScrollState())
                            }
                            .reorderable(reorderState)
                            .detectReorderAfterLongPress(reorderState)
                    } else modifier
                        .conditional(scrollableContent) {
                            verticalScroll(rememberScrollState())
                        },
                ) {
                    content!!()
                }
            }

            if (bottomContent != null) {
                Spacer(Modifier.weight(1f))
                bottomContent()
            }
        }
    }

    if (showHelpDialog) {
        UserValidation(
            title = "$title ${stringResource(R.string.help)}",
            message = helpText,
            onDismiss = { showHelpDialog = false },
            titleIcon = Icons.AutoMirrored.Filled.Help,
            titleColor = MaterialTheme.colorScheme.onSurface
        ) {
            showHelpDialog = false
        }
    }
    if (showResetDialog && resetText != null && onReset != null) {
        UserValidation(
            title = resetTitle,
            message = resetText,
            onDismiss = { showResetDialog = false }
        ) {
            onReset()
            showResetDialog = false
        }
    }
}
