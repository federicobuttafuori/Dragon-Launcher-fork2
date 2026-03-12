package org.elnix.dragonlauncher.common.serializables

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.utils.Constants.Logging.SWIPE_TAG
import org.elnix.dragonlauncher.common.utils.SETTINGS
import java.util.UUID


fun dummySwipePoint(
    action: SwipeActionSerializable? = null,
    id: String? = null
) =
    SwipePointSerializable(
        circleNumber = 0,
        angleDeg = 0.0,
        action = action ?: SwipeActionSerializable.OpenDragonLauncherSettings(),
        id = id ?: UUID.randomUUID().toString(),
        nestId = 0
    )

val defaultSwipePointsValues = dummySwipePoint(null).copy(
    borderStroke = 4f,
    borderStrokeSelected = 8f,
    opacity = 1f,
    cornerRadius = null,
    innerPadding = 5,
    size = 22,
    borderShape = IconShape.Circle,
    borderShapeSelected = IconShape.Circle
)


/**
 * Swipe Actions Serializable, the core of the main gesture idea
 * Holds all the different actions the user can do
 */
@Serializable
sealed class SwipeActionSerializable {
    @Serializable
    data class LaunchApp(
        val packageName: String,
        val isPrivateSpace: Boolean,
        val userId: Int?
    ) : SwipeActionSerializable()

    @Serializable
    data class LaunchShortcut(
        val packageName: String,
        val shortcutId: String
    ) : SwipeActionSerializable()

    @Serializable
    data class OpenUrl(val url: String) : SwipeActionSerializable()
    @Serializable
    data class OpenFile(
        val uri: String,
        val mimeType: String? = null
    ) : SwipeActionSerializable()

    @Serializable
    object NotificationShade : SwipeActionSerializable()
    @Serializable
    object ControlPanel : SwipeActionSerializable()
    @Serializable
    data class OpenAppDrawer(val workspaceId: String? = null) : SwipeActionSerializable()
    @Serializable
    data class OpenDragonLauncherSettings(val route: String = SETTINGS.ROOT) : SwipeActionSerializable()
    @Serializable
    object Lock : SwipeActionSerializable()
    @Serializable
    object ReloadApps : SwipeActionSerializable()

    @Serializable
    object OpenRecentApps : SwipeActionSerializable()
    @Serializable
    data class OpenCircleNest(val nestId: Int) : SwipeActionSerializable()
    @Serializable
    object GoParentNest : SwipeActionSerializable()

    @Serializable
    data class OpenWidget(
        val widgetId: Int,
        val providerPackage: String,
        val providerClass: String
    ) : SwipeActionSerializable()

    @Serializable
object None : SwipeActionSerializable()
}

object SwipeJson {
    private val jsonConfig = Json {
        encodeDefaults = true
        explicitNulls = false
        ignoreUnknownKeys = true
    }

    private val jsonPretty = Json {
        encodeDefaults = true
        explicitNulls = false
        ignoreUnknownKeys = true
        prettyPrint = true
    }


    /* ───────────── Points ───────────── */

    fun encodePoints(points: List<SwipePointSerializable>): String =
        jsonConfig.encodeToString(points)

    fun encodePointsPretty(points: List<SwipePointSerializable>): String =
        jsonPretty.encodeToString(points)

    fun decodePoints(json: String): List<SwipePointSerializable> {
        return try {
            jsonConfig.decodeFromString<List<SwipePointSerializable>>(json)
                .takeIf { it.isNotEmpty() }
                // If empty, but no errors reported, try the old method anyways
                ?: LegacySwipeJson.decodePoints(json)
        } catch (e: Exception) {
            logE(SWIPE_TAG, e) { "Modern points decode failed, trying legacy method" }

            // Decode the ol' way, returns emptyList if fails. (Skill issue if it happens)
            LegacySwipeJson.decodePoints(json)
        }
    }

    /* ───────────── Nests ───────────── */

    fun encodeNests(nests: List<CircleNest>): String =
        jsonConfig.encodeToString(nests)

    fun encodeNestsPretty(nests: List<CircleNest>): String =
        jsonPretty.encodeToString(nests)

    fun decodeNests(json: String): List<CircleNest> {
        if (json.isBlank()) return emptyList()
        return try {
            jsonConfig.decodeFromString(json)
        } catch (e: Exception) {
            logE(SWIPE_TAG, e) { "Modern nest decode failed, trying legacy" }

            // Decode the ol' way, returns emptyList if fails. (Skill issue if it happens)
            LegacySwipeJson.decodeNests(json)
        }
    }

    fun encodeAction(action: SwipeActionSerializable?): String? =
        action?.let {
            jsonConfig.encodeToString(it)
        }

    fun decodeAction(jsonString: String): SwipeActionSerializable? {
        if (jsonString.isBlank() || jsonString == "{}") return null
        return try {
            jsonConfig.decodeFromString<SwipeActionSerializable>(jsonString)
        } catch (_: Throwable) {
            LegacySwipeJson.decodeAction(jsonString)
        }
    }
}
