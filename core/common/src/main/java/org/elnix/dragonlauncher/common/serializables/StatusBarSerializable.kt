package org.elnix.dragonlauncher.common.serializables

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.utils.Constants.Logging.STATUS_BAR_TAG
import org.elnix.dragonlauncher.common.utils.isBlankJson
import java.lang.reflect.Type


sealed class StatusBarSerializable {

    data class Time(
        val formatter: String = "HH:mm:ss",
        val action: SwipeActionSerializable? = null
    ) : StatusBarSerializable()

    data class Date(
        val formatter: String = "MMM dd",
        val action: SwipeActionSerializable? = null
    ) : StatusBarSerializable()

    data class Bandwidth(
        val merge: Boolean = false
    ) : StatusBarSerializable()

    data class Notifications(
        val maxIcons: Int = 8
    ) : StatusBarSerializable()

    data class Connectivity(
        val showAirplaneMode: Boolean = true,
        val showWifi: Boolean = true,
        val showBluetooth: Boolean = true,
        val showVpn: Boolean = true,
        val showMobileData: Boolean = true,
        val showHotspot: Boolean = true,
        val updateFrequency: Int = 5
    ) : StatusBarSerializable()

    data class Spacer(
        val width: Int = -1
    ) : StatusBarSerializable()

    data class Battery(
        val showIcon: Boolean = false,
        val showPercentage: Boolean = true
    ) : StatusBarSerializable()

    data class NextAlarm(
        val formatter: String = "HH:mm"
    ) : StatusBarSerializable()
}


val allStatusBarSerializable = listOf(
    StatusBarSerializable.Time(),
    StatusBarSerializable.Date(),
    StatusBarSerializable.Bandwidth(),
    StatusBarSerializable.Notifications(),
    StatusBarSerializable.Connectivity(),
    StatusBarSerializable.Battery(),
    StatusBarSerializable.NextAlarm(),
    StatusBarSerializable.Spacer()
)

// Gson type adapter for sealed class
class StatusBarAdapter : JsonSerializer<StatusBarSerializable>, JsonDeserializer<StatusBarSerializable> {
    override fun serialize(
        src: StatusBarSerializable?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        if (src == null) return JsonNull.INSTANCE
        val obj = JsonObject()
        when (src) {

            is StatusBarSerializable.Time -> {
                obj.addProperty("type", "Time")
                val actionJson = SwipeJson.encodeAction(src.action)
                if (actionJson != null) {
                    obj.add("action", JsonParser.parseString(actionJson))
                }
                obj.addProperty("formatter", src.formatter)
            }

            is StatusBarSerializable.Date -> {
                obj.addProperty("type", "Date")
                val actionJson = SwipeJson.encodeAction(src.action)
                if (actionJson != null) {
                    obj.add("action", JsonParser.parseString(actionJson))
                }
                obj.addProperty("formatter", src.formatter)
            }

            is StatusBarSerializable.Bandwidth -> {
                obj.addProperty("type", "Bandwidth")
                obj.addProperty("merge", src.merge)
            }

            is StatusBarSerializable.Notifications -> {
                obj.addProperty("type", "Notifications")
                obj.addProperty("maxIcons", src.maxIcons)
            }

            is StatusBarSerializable.Connectivity -> {
                obj.addProperty("type", "Connectivity")
                obj.addProperty("showAirplaneMode", src.showAirplaneMode)
                obj.addProperty("showWifi", src.showWifi)
                obj.addProperty("showBluetooth", src.showBluetooth)
                obj.addProperty("showVpn", src.showVpn)
                obj.addProperty("showMobileData", src.showMobileData)
                obj.addProperty("showHotspot", src.showHotspot)
                obj.addProperty("updateFrequency", src.updateFrequency)
            }

            is StatusBarSerializable.Spacer -> {
                obj.addProperty("type", "Spacer")
                obj.addProperty("width", src.width)
            }

            is StatusBarSerializable.Battery -> {
                obj.addProperty("type", "Battery")
                obj.addProperty("showIcon", src.showIcon)
                obj.addProperty("showPercentage", src.showPercentage)
            }

            is StatusBarSerializable.NextAlarm -> {
                obj.addProperty("type", "NextAlarm")
                obj.addProperty("formatter", src.formatter)
            }
        }
        return obj
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): StatusBarSerializable? {
        if (json == null || !json.isJsonObject) return null
        val obj = json.asJsonObject
        val type = if (obj.has("type")) obj.get("type").asString else return null

        return try {
            when (type) {
                "Time" -> StatusBarSerializable.Time(
                    formatter = if (obj.has("formatter")) obj.get("formatter").asString else "HH:mm:ss",
                    action = if (obj.has("action") && !obj.get("action").isJsonNull) {
                        SwipeJson.decodeAction(obj.get("action").toString())
                    } else null
                )

                "Date" -> StatusBarSerializable.Date(
                    formatter = if (obj.has("formatter")) obj.get("formatter").asString else "MMM dd",
                    action = if (obj.has("action") && !obj.get("action").isJsonNull) {
                        SwipeJson.decodeAction(obj.get("action").toString())
                    } else null
                )

                "Bandwidth" -> StatusBarSerializable.Bandwidth(
                    merge = if (obj.has("merge")) obj.get("merge").asBoolean else false
                )

                "Notifications" -> StatusBarSerializable.Notifications(
                    maxIcons = if (obj.has("maxIcons")) obj.get("maxIcons").asInt else 8
                )

                "Connectivity" -> StatusBarSerializable.Connectivity(
                    showAirplaneMode = if (obj.has("showAirplaneMode")) obj.get("showAirplaneMode").asBoolean else true,
                    showWifi = if (obj.has("showWifi")) obj.get("showWifi").asBoolean else true,
                    showBluetooth = if (obj.has("showBluetooth")) obj.get("showBluetooth").asBoolean else true,
                    showVpn = if (obj.has("showVpn")) obj.get("showVpn").asBoolean else true,
                    showMobileData = if (obj.has("showMobileData")) obj.get("showMobileData").asBoolean else true,
                    showHotspot = if (obj.has("showHotspot")) obj.get("showHotspot").asBoolean else true,
                    updateFrequency = if (obj.has("updateFrequency")) obj.get("updateFrequency").asInt else 5,
                )

                "Spacer" -> StatusBarSerializable.Spacer(
                    width = if (obj.has("width")) obj.get("width").asInt else -1
                )

                "Battery" -> StatusBarSerializable.Battery(
                    showIcon = if (obj.has("showIcon")) obj.get("showIcon").asBoolean else false,
                    showPercentage = if (obj.has("showPercentage")) obj.get("showPercentage").asBoolean else true,
                )

                "NextAlarm" -> StatusBarSerializable.NextAlarm(
                    formatter = if (obj.has("formatter")) obj.get("formatter").asString else "HH:mm"
                )

                else -> null
            }
        } catch (e: Exception) {
            logE(STATUS_BAR_TAG) { "Deserialization error for type $type: ${e.message}" }
            null
        }
    }
}

object StatusBarJson {
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(StatusBarSerializable::class.java, StatusBarAdapter())
        .create()

    private val type = object : TypeToken<List<StatusBarSerializable>>() {}.type


    /* ───────────── List encoders / decoders ───────────── */

    fun encodeStatusBarElements(elements: List<StatusBarSerializable>): String =
        gson.toJson(elements, type)


    fun decodeStatusBarElements(json: String?): List<StatusBarSerializable> {
        if (json == null || json.isBlankJson) return emptyList()

        StatusBarJson.logD(STATUS_BAR_TAG) { json }
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Throwable) { // Catch Throwable to be safe against everything
            StatusBarJson.logE(STATUS_BAR_TAG) { "Decode failed for JSON: $json" }
            emptyList()
        }
    }
}
