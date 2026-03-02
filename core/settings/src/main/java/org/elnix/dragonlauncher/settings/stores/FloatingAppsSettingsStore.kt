package org.elnix.dragonlauncher.settings.stores

import android.content.Context
import org.elnix.dragonlauncher.common.FloatingAppObject
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.SwipeJson
import org.elnix.dragonlauncher.common.utils.Constants.Logging.FLOATING_APPS_TAG
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.JsonObjectSettingsStore
import org.json.JSONArray
import org.json.JSONObject
import java.util.Collections.emptyList

object FloatingAppsSettingsStore : JsonObjectSettingsStore() {

    override val name: String = "Floating Apps"
    override val dataStoreName: DataStoreName
        get() = DataStoreName.FLOATING_APPS


    override val jsonSetting = Settings.string(
        key = "floating_apps",
        dataStoreName = dataStoreName,
        default = ""
    )

    override val ALL: List<BaseSettingObject<*,*>>
        get() = listOf(jsonSetting)

    suspend fun loadFloatingApps(ctx: Context): List<FloatingAppObject> {
        return try {
            val allJson = getAll(ctx)
            logD(FLOATING_APPS_TAG, "Raw: $allJson")
            val floatingAppArray = allJson?.optJSONArray("floating_apps") ?: return emptyList()

            val floatingApps = mutableListOf<FloatingAppObject>()
            for (i in 0 until floatingAppArray.length()) {
                val obj = floatingAppArray.getJSONObject(i)

                floatingApps.add(
                    FloatingAppObject(
                        id = obj.getInt("id"),
                        appWidgetId = if (obj.has("appWidgetId")) obj.getInt("appWidgetId") else null,
                        nestId = obj.getInt("nestId"),
                        action = SwipeJson.decodeAction(obj.getString("action"))
                            ?: SwipeActionSerializable.LaunchApp(ctx.packageName, false, 0),
                        spanX = obj.optDouble("spanX", 1.0).toFloat(),
                        spanY = obj.optDouble("spanY", 1.0).toFloat(),
                        x = obj.optDouble("x", 0.0).toFloat(),
                        y = obj.optDouble("y", 0.0).toFloat(),
                        angle = obj.optDouble("angle", 0.0),
                        ghosted = obj.optBoolean("ghosted", false),
                        foreground = obj.optBoolean("foreground", true)
                    )
                )
            }
            logD(FLOATING_APPS_TAG, "Loaded ${floatingApps.size} floatingApps")
            floatingApps
        } catch (e: Exception) {
            logE(FLOATING_APPS_TAG, "Load failed", e)
            emptyList()
        }
    }

    suspend fun saveFloatingApp(ctx: Context, floatingApp: FloatingAppObject) {
        logD(FLOATING_APPS_TAG, "Saving floatingApps ${floatingApp.id}")

        val floatingApps = loadFloatingApps(ctx).toMutableList().apply {
            removeAll { it.id == floatingApp.id }
            add(floatingApp)
        }

        val floatingAppsArray = JSONArray().apply {
            floatingApps.forEach { floatingApp ->
                put(JSONObject().apply {
                    put("id", floatingApp.id)
                    put("appWidgetId", floatingApp.appWidgetId)
                    put("nestId", floatingApp.nestId)
                    put("action", SwipeJson.encodeAction(floatingApp.action))
                    put("spanX", floatingApp.spanX)
                    put("spanY", floatingApp.spanY)
                    put("x", floatingApp.x)
                    put("y", floatingApp.y)
                    put("angle", floatingApp.angle)
                    put("ghosted", floatingApp.ghosted)
                    put("foreground", floatingApp.foreground)
                })
            }
        }

        val json = JSONObject().apply {
            put("floating_apps", floatingAppsArray)
        }

        logD(FLOATING_APPS_TAG, "Saved: $json")
        setAll(ctx, json)
    }

    suspend fun deleteFloatingApp(ctx: Context, id: Int) {
        val floatingApps = loadFloatingApps(ctx).filterNot { it.id == id }

        val floatingAppsArray = JSONArray().apply {
            floatingApps.forEach { floatingApp ->
                put(JSONObject().apply {
                    put("id", floatingApp.id)
                    put("nestId", floatingApp.nestId)
                    put("action", SwipeJson.encodeAction(floatingApp.action))
                    put("spanX", floatingApp.spanX)
                    put("spanY", floatingApp.spanY)
                    put("x", floatingApp.x)
                    put("y", floatingApp.y)
                    put("angle", floatingApp.angle)
                    put("ghosted", floatingApp.ghosted)
                    put("foreground", floatingApp.foreground)
                })
            }
        }

        val json = JSONObject().apply {
            put("floating_apps", floatingAppsArray)
        }

        setAll(ctx, json)
    }
}
