package org.elnix.dragonlauncher.services

import android.content.Context
import android.content.Intent
import android.net.Uri
import org.elnix.dragonlauncher.common.serializables.ExtensionModel
import org.elnix.dragonlauncher.common.utils.PackageManagerCompat
import org.elnix.dragonlauncher.common.utils.openUrl

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

    fun isExtensionInstalled(context: Context, packageName: String): Boolean {
        return PackageManagerCompat(context.packageManager, context).isPackageInstalled(packageName)
    }
}
