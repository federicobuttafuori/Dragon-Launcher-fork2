package org.elnix.dragonlauncher.common.serializables

import com.google.gson.annotations.SerializedName

data class ExtensionModel(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("package") val packageName: String?,
    @SerializedName("version") val version: String?,
    @SerializedName("descriptions") val description: Map<String, String>,
    @SerializedName("author") val author: String?,
    @SerializedName("license") val license: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("download_url") val downloadUrl: String,
    @SerializedName("additional_permissions") val permissions: List<String> = emptyList()
)
