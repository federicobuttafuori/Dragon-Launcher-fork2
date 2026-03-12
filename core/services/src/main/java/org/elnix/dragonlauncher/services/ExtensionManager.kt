package org.elnix.dragonlauncher.services

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.net.toUri
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.logging.logW
import org.elnix.dragonlauncher.common.serializables.ExtensionModel
import org.elnix.dragonlauncher.common.utils.Constants.Logging.EXTENSION_MANAGER_TAG
import org.elnix.dragonlauncher.common.utils.PackageManagerCompat
import org.elnix.dragonlauncher.common.utils.openUrl
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.settings.stores.DebugSettingsStore

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
            if (!context.packageManager.canRequestPackageInstalls()) {
                val intent = Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = "package:${context.packageName}".toUri()
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
            context.showToast("Failed to install APK")
        }
    }

    fun isExtensionInstalled(context: Context, packageNameOrId: String): Boolean {
        val pmCompat = PackageManagerCompat(context.packageManager, context)
        logD(EXTENSION_MANAGER_TAG) { "Checking extension installed for: $packageNameOrId" }

        val disableSigCheck = kotlinx.coroutines.runBlocking {
            DebugSettingsStore.disableExtensionSignatureCheck.get(context)
        }

        // 1. Direct check with provided packageName or ID (The correct way)
        try {
//            val pInfo = context.packageManager.getPackageInfo(packageNameOrId, 0)
            
            // Signature check
            if (!disableSigCheck) {
                @Suppress("DEPRECATION")
                val myPkgInfo = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
                @Suppress("DEPRECATION")
                val targetPkgInfo = context.packageManager.getPackageInfo(packageNameOrId, PackageManager.GET_SIGNATURES)
                
                val mySignatures = myPkgInfo.signatures
                val targetSignatures = targetPkgInfo.signatures
                
                val mySig = mySignatures?.firstOrNull()?.toCharsString()
                val targetSig = targetSignatures?.firstOrNull()?.toCharsString()

                if (mySig == null || mySig != targetSig) {
                    logW(EXTENSION_MANAGER_TAG) { "Signature mismatch for $packageNameOrId! Blocking detection. Enable 'Disable extension signature check' in debug to bypass." }
                    return false
                }
            }

            logD(EXTENSION_MANAGER_TAG) { "Direct package present: $packageNameOrId" }
            return true
        } catch (_: Exception) {
            // ignore
        }

        // 2. Fallback: If it's an ID (like "fonts"), search for org.elnix.dragonlauncher.extension.ID
        val standardPkg = "org.elnix.dragonlauncher.extension.$packageNameOrId"
        if (packageNameOrId != standardPkg && pmCompat.isPackageInstalled(standardPkg)) {
             logD(EXTENSION_MANAGER_TAG) { "Found via standard prefix: $standardPkg" }
             return true
        }

        return false
    }
}
