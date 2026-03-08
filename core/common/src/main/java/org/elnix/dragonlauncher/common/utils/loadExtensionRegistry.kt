package org.elnix.dragonlauncher.common.utils

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.elnix.dragonlauncher.common.serializables.ExtensionRegistry
import java.io.InputStreamReader

suspend fun loadExtensionRegistry(context: Context): ExtensionRegistry? = withContext(Dispatchers.IO) {
    try {
        context.assets.open("extensions-registry.json").use { inputStream ->
            val reader = InputStreamReader(inputStream)
            Gson().fromJson(reader, ExtensionRegistry::class.java)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
