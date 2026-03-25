package org.elnix.dragonlauncher.ui.components.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject

/**
 * Collects the current value of this setting as a Compose [State], using a default value if none is set.
 *
 * This is useful for observing the setting in Composables and triggering recompositions automatically
 * when the value changes.
 *
 * @param default Optional default value to use before the first emission. If null, the setting's own default is used.
 * @return A [State] holding the current value of the setting.
 */
@Composable
fun <T, R> BaseSettingObject<T, R>.asState(default: T? = null): State<T> {
    val ctx = LocalContext.current
    return flow(ctx).collectAsState(initial = default ?: this.default)
}

/**
 * Collects the current value of this setting as a Compose [State] that allows null values.
 *
 * Unlike [asState], this version always starts with `null` and can represent an unset state explicitly.
 * Useful when `null` has semantic meaning in your UI.
 *
 * @return A [State] holding the current value of the setting, or null if not yet set.
 */
@Composable
fun <T, R> BaseSettingObject<T, R>.asStateNull(): State<T?> {
    val ctx = LocalContext.current
    return flow(ctx).map { it }.collectAsState(initial = null)
}

/**
 * Updates the value of this setting from within a Compose context using a coroutine.
 *
 * This provides an ergonomic way to write to settings directly in Composable, without manually
 * managing [android.content.Context] or [kotlinx.coroutines.CoroutineScope]. Launches a coroutine scoped to the current Composable.
 *
 * Example:
 * ```
 * store.primaryColor(newValue)
 * ```
 *
 * @param value The new value to set. Can be null to reset the setting to its default.
 */
@Composable
operator fun <T, R> BaseSettingObject<T, R>.invoke(value: T?) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            set(ctx, value)
        }
    }

    return
}
