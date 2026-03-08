package org.elnix.dragonlauncher.common.serializables

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Describes a highly customizable icon that can be rendered from multiple sources
 * (vector, bitmap, text, or procedural shape) with advanced visual controls.
 *
 * This model is renderer-agnostic and supports extreme theming and animation use cases.
 */
@Serializable
data class CustomIconSerializable(

    /** Icon source type determining how `source` is interpreted. */
    @SerialName("a")
    val type: IconType? = null,

    /**
     * Icon source reference.
     * - BITMAP: base64-encoded image
     * - ICON_PACK: base64-encoded image
     * - TEXT: emoji or glyph
     * - SHAPE: renderer-defined primitive
     */
    @SerialName("b")
    val source: String? = null,

    /** Tint color (ARGB) applied after rendering. */
    @SerialName("c")
    val tint: Int? = null,

    /** Icon opacity multiplier (0.0 – 1.0). */
    @SerialName("d")
    val opacity: Float? = null,

    /** Per-corner radius override for icon clipping. */
    @SerialName("shape")
    val shape: IconShape? = null,

    /** Stroke width (dp) around the icon shape. */
    @SerialName("g")
    val strokeWidth: Float? = null,

    /** Stroke color (ARGB) around the icon. */
    @SerialName("h")
    val strokeColor: Long? = null,

    /** Blur radius for icon shadow. */
    @SerialName("i")
    val shadowRadius: Float? = null,

    /** Shadow color (ARGB). */
    @SerialName("j")
    val shadowColor: Long? = null,

    /** Horizontal shadow offset (dp). */
    @SerialName("k")
    val shadowOffsetX: Float? = null,

    /** Vertical shadow offset (dp). */
    @SerialName("l")
    val shadowOffsetY: Float? = null,

    /** Rotation applied to the icon in degrees. */
    @SerialName("m")
    val rotationDeg: Float? = null,

    /** Horizontal scale multiplier. */
    @SerialName("n")
    val scaleX: Float? = null,

    /** Vertical scale multiplier. */
    @SerialName("o")
    val scaleY: Float? = null,

    /** Optional blend mode name (renderer-defined, e.g. SRC_IN, MULTIPLY). */
    @SerialName("p")
    val blendMode: String? = null
)

/**
 * Defines how a custom icon should be interpreted and rendered.
 */
@Serializable
enum class IconType {

    /** Icon sourced from an installed icon pack. */
    ICON_PACK,

    /** icon sourced from and image (PNG, JPG, WEBP). */
    BITMAP,

    /** Text-based icon (emoji, glyph, font icon). */
    TEXT,
    PLAIN_COLOR
}

/**
 * Custom BlendModes usd in the icon resolution
 * @see org.elnix.dragonlauncher.common.utils.ImageUtils.resolveCustomIconBitmap
 */
@Serializable
enum class BlendModes {
    DEFAULT,
    MULTIPLY,
    SCREEN,
    OVERLAY
}
