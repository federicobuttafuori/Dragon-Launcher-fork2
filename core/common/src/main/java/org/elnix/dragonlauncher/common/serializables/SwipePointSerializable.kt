package org.elnix.dragonlauncher.common.serializables

import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable.OpenDragonLauncherSettings

/**
 * Serializable model representing a single swipe point on a radial / circular UI.
 *
 * This object is intentionally compact and fully nullable-extensible to allow
 * backward-compatible evolution of visual, behavioral, and interaction features.
 *
 * All visual values are interpreted by the rendering layer (Compose / Canvas / View).
 */
@Immutable
@Serializable
data class SwipePointSerializable(

    /** Index of the circle (ring) this swipe point belongs to. */
    @SerializedName("a")
    var circleNumber: Int,

    /** Angular position in degrees (0–360), clockwise, relative to the circle center. */
    @SerializedName("b")
    var angleDeg: Double,

    /** Optional action executed when the swipe point is triggered. */
    @SerializedName("c")
    val action: SwipeActionSerializable,

    /** Stable unique identifier for persistence, diffing, and migrations. */
    @SerializedName("d")
    val id: String,

    /** Optional nesting/group identifier for hierarchical or contextual swipe layouts. */
    @SerializedName("e")
    var nestId: Int? = 0,

    /** Fully customizable icon definition overriding default visuals. */
    @SerializedName("f")
    val customIcon: CustomIconSerializable? = null,

    /** Border thickness (dp) when the swipe point is not selected. */
    @SerializedName("g")
    val borderStroke: Float? = null,

    /** Border thickness (dp) when the swipe point is selected or active. */
    @SerializedName("h")
    val borderStrokeSelected: Float? = null,

    /** Border color in ARGB format when not selected. */
    @SerializedName("i")
    val borderColor: Int? = null,

    /** Background fill color (ARGB) in normal state. */
    @SerializedName("k")
    val backgroundColor: Int? = null,

    /** Border color in ARGB format when selected. */
    @SerializedName("j")
    val borderColorSelected: Int? = null,

    /** Background fill color (ARGB) in selected state. */
    @SerializedName("l")
    val backgroundColorSelected: Int? = null,

    /** Global opacity multiplier (0.0 – 1.0) applied to the whole swipe point. */
    @SerializedName("m")
    val opacity: Float? = null,

    /**
     * Fully customizable haptic feedback generator.
     * Stores the setting in a map of [Boolean] to [Int].
     * when the boolean is true, it indicates a vibration, and when off a pause.
     * the [Int] value is the duration of the vibration
     */
    // No Serialized name, as it was the previous version, and it's a new setting
    val hapticFeedback: CustomHapticFeedbackSerializable? = null,


    /** Optional user-defined display name (labels, accessibility, debug UI). */
    @SerializedName("o")
    val customName: String? = null,

    /** Per-corner radius definition for the swipe point container. */
    @SerializedName("p")
    val cornerRadius: CornerRadiusSerializable? = null,

    /** Inner padding (dp) between border and content. */
    @SerializedName("q")
    val innerPadding: Int? = null,

    /** Optional override for action color, default (null) will use the action color */
    @SerializedName("r")
    val customActionColor: Int? = null,

    /** Optional size override */
    @SerializedName("s")
    val size: Int? = null,

    /**
     * Shape of the border icon, default is a circle
     */
    @SerializedName("borderShape")
    val borderShape: IconShape? = null,

    /**
     * Shape of the selected border icon, default is a circle
     */
    @SerializedName("borderShapeSelected")
    val borderShapeSelected: IconShape? = null
) {

    fun toShortString(): String =
        "action: $action | nestId: $nestId | angle: $angleDeg | circleNumber: $circleNumber"

}


fun SwipePointSerializable.applyColorAction(): Boolean = (
            action !is SwipeActionSerializable.LaunchApp &&
            action !is SwipeActionSerializable.LaunchShortcut &&
            action !is OpenDragonLauncherSettings
    ) &&
    // Checks if the source is null (no custom icons, image, text or pack)
    // to avoid drawing the tint on the rendered icon
    customIcon?.type == null
