package org.elnix.dragonlauncher.common.serializables

import com.google.gson.annotations.SerializedName

data class ExtensionRegistry(
    @SerializedName("extensions") val extensions: List<ExtensionModel>
)

data class ExtensionModel(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("packageName") val packageName: String,
    @SerializedName("version") val version: String,
    @SerializedName("description") val description: Map<String, String>,
    @SerializedName("author") val author: String,
    @SerializedName("license") val license: String,
    @SerializedName("url") val url: String,
    @SerializedName("downloadUrl") val downloadUrl: String,
    @SerializedName("permissions") val permissions: List<String>
)
