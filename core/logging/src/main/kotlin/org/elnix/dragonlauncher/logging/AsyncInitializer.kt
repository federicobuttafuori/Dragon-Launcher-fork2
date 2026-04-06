package org.elnix.dragonlauncher.logging

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Handles non-critical background initialization to speed up app startup.
 */
object AsyncInitializer {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _isInitialized = MutableStateFlow(false)

    fun init(ctx: Context) {
        scope.launch {
            val startTime = System.currentTimeMillis()

            // 1. Logging (Critical but fast)
            DragonLogManager.init(ctx)
            Timber.d("AsyncInitializer: DragonLogManager initialized")

            // 2. Add other non-critical heavy initializations here
            // e.g., Database pre-warming, SDK initializations, etc.

            _isInitialized.value = true
            Timber.d("AsyncInitializer: Finished in ${System.currentTimeMillis() - startTime}ms")
        }
    }
}