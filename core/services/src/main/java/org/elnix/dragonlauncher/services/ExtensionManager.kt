package org.elnix.dragonlauncher.services

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import org.elnix.dragonlauncher.common.serializables.ExtensionModel
import org.elnix.dragonlauncher.common.utils.PackageManagerCompat
import org.elnix.dragonlauncher.common.utils.openUrl
import org.elnix.dragonlauncher.common.utils.showToast

object ExtensionManager {
    private const val INTERNET_EXTENSION_PKG = "org.elnix.dragonlauncher.extension.internet"
    private const val SHIZUKU_EXTENSION_PKG = "org.elnix.dragonlauncher.extension.shizuku"

    fun installExtension(context: Context, extension: ExtensionModel) {
        val pmCompat = PackageManagerCompat(context.packageManager, context)
        
        val hasInternetExt = pmCompat.isPackageInstalled(INTERNET_EXTENSION_PKG)
        val hasShizukuExt = pmCompat.isPackageInstalled(SHIZUKU_EXTENSION_PKG)

        when {
            hasInternetExt -> {
                // TODO: Download direct via extension service and install
                // For now, fallback to browser but mark as "via Internet Extension" logic path
                context.openUrl(extension.downloadUrl)
            }
            hasShizukuExt -> {
                // TODO: Download and specialized Shizuku install
                context.openUrl(extension.downloadUrl)
            }
            else -> {
                // Fallback to browser
                context.openUrl(extension.downloadUrl)
            }
        }
    }

    fun installApk(context: Context, uri: Uri) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !context.packageManager.canRequestPackageInstalls()) {
                val intent = Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                context.showToast("Please allow unknown app installs first")
                return
            }

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            context.showToast("Failed to install APK: ${e.message}")
        }
    }

    fun isExtensionInstalled(context: Context, packageNameOrId: String): Boolean {
        val pmCompat = PackageManagerCompat(context.packageManager, context)
        Log.d("ExtensionManager", "Checking extension installed for: $packageNameOrId")

        // 1. Direct check with provided packageName or ID (The correct way)
        try {
            if (pmCompat.isPackageInstalled(packageNameOrId)) {
                Log.d("ExtensionManager", "Direct package present: $packageNameOrId")
                return true
            }
        } catch (e: Exception) {
            // ignore
        }

        // 2. Fallback: If it's an ID (like "fonts"), search for org.elnix.dragonlauncher.extension.ID
        val standardPkg = "org.elnix.dragonlauncher.extension.$packageNameOrId"
        if (packageNameOrId != standardPkg && pmCompat.isPackageInstalled(standardPkg)) {
             Log.d("ExtensionManager", "Found via standard prefix: $standardPkg")
             return true
        }

        return false
    }
}
