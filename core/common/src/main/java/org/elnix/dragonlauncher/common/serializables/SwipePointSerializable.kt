package org.elnix.dragonlauncher.common.serializables

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
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
    @SerialName("a")
    var circleNumber: Int,

    /** Angular position in degrees (0–360), clockwise, relative to the circle center. */
    @SerialName("b")
    var angleDeg: Double,

    /** Optional action executed when the swipe point is triggered. */
    @SerialName("c")
    val action: SwipeActionSerializable,

    /** Stable unique identifier for persistence, diffing, and migrations. */
    @SerialName("d")
    val id: String,

    /** Optional nesting/group identifier for hierarchical or contextual swipe layouts. */
    @SerialName("e")
    var nestId: Int? = 0,

    /** Fully customizable icon definition overriding default visuals. */
    @SerialName("f")
    val customIcon: CustomIconSerializable? = null,

    /** Border thickness (dp) when the swipe point is not selected. */
    @SerialName("g")
    val borderStroke: Float? = null,

    /** Border thickness (dp) when the swipe point is selected or active. */
    @SerialName("h")
    val borderStrokeSelected: Float? = null,

    /** Border color in ARGB format when not selected. */
    @SerialName("i")
    val borderColor: Int? = null,

    /** Background fill color (ARGB) in normal state. */
    @SerialName("k")
    val backgroundColor: Int? = null,

    /** Border color in ARGB format when selected. */
    @SerialName("j")
    val borderColorSelected: Int? = null,

    /** Background fill color (ARGB) in selected state. */
    @SerialName("l")
    val backgroundColorSelected: Int? = null,

    /** Global opacity multiplier (0.0 – 1.0) applied to the whole swipe point. */
    @SerialName("m")
    val opacity: Float? = null,

    /** Enables haptic feedback when the swipe point is activated. */
    @SerialName("n")
    val haptic: Int? = null,

    /** Optional user-defined display name (labels, accessibility, debug UI). */
    @SerialName("o")
    val customName: String? = null,

    /** Per-corner radius definition for the swipe point container. */
    @SerialName("p")
    val cornerRadius: CornerRadiusSerializable? = null,

    /** Inner padding (dp) between border and content. */
    @SerialName("q")
    val innerPadding: Int? = null,

    /** Optional override for action color, default (null) will use the action color */
    @SerialName("r")
    val customActionColor: Int? = null,

    /** Optional size override */
    @SerialName("s")
    val size: Int? = null,

    /**
     * Shape of the border icon, default is a circle
     */
    @SerialName("borderShape")
    val borderShape: IconShape? = IconShape.Circle,

    /**
     * Shape of the selected border icon, default is a circle
     */
    @SerialName("borderShapeSelected")
    val borderShapeSelected: IconShape? = IconShape.Circle
) {
    override fun toString(): String {
        return "action: $action | nestId: $nestId | angle: $angleDeg | circleNumber: $circleNumber"
    }
}


fun SwipePointSerializable.applyColorAction(): Boolean = (
            action !is SwipeActionSerializable.LaunchApp &&
            action !is SwipeActionSerializable.LaunchShortcut &&
            action !is OpenDragonLauncherSettings
    ) &&
    // Checks if the source is null (no custom icons, image, text or pack)
    // to avoid drawing the tint on the rendered icon
    customIcon?.type == null
