package org.elnix.dragonlauncher.common.utils

import android.content.Context
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.logging.DragonLogManager
import timber.log.Timber

/**
 * Handles non-critical background initialization to speed up app startup.
 */
object AsyncInitializer {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized

    fun init(context: Context) {
        scope.launch {
            val startTime = System.currentTimeMillis()
            
            // 1. Logging (Critical but fast)
            DragonLogManager.init(context)
            Timber.d("AsyncInitializer: DragonLogManager initialized")

            // 2. Add other non-critical heavy initializations here
            // e.g., Database pre-warming, SDK initializations, etc.

            _isInitialized.value = true
            Timber.d("AsyncInitializer: Finished in ${System.currentTimeMillis() - startTime}ms")
        }
    }
}
