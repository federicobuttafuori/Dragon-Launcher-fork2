package org.elnix.dragonlauncher.common.utils.colors

import androidx.compose.ui.graphics.Color

object ColorUtils {
//    fun Color.blendWith(other: Color, ratio: Float): Color {
//        return Color(
//            red = red * (1 - ratio) + other.red * ratio,
//            green = green * (1 - ratio) + other.green * ratio,
//            blue = blue * (1 - ratio) + other.blue * ratio,
//            alpha = alpha
//        )
//    }

//    fun Color.adjustBrightness(
//        factor: Float,
//        affectAlpha: Boolean = false
//    ): Color {
//        return Color(
//            red = (red * factor).coerceIn(0f, 1f),
//            green = (green * factor).coerceIn(0f, 1f),
//            blue = (blue * factor).coerceIn(0f, 1f),
//            alpha = if (affectAlpha) (alpha * factor).coerceIn(0f, 1f) else alpha
//        )
//    }


    /**
     * Returns this [Color] only if it is explicitly defined.
     *
     * If the receiver is `null` or equal to [Color.Unspecified], this returns `null`.
     * Otherwise, it returns the receiver unchanged.
     *
     * This is useful when treating [Color.Unspecified] as an absent value
     * and normalizing it to `null` for clearer nullable handling.
     *
     * @return this color if defined, or `null` if it is `null` or `Color.Unspecified`
     */
    fun Color?.definedOrNull(): Color? =
        this.takeIf { it != Color.Unspecified }


    /**
     * Returns this [Color] if it is non-null, or [default] otherwise.
     *
     * This is a convenience extension for providing a fallback color when
     * working with nullable [Color] values.
     *
     * Note that this does not treat [Color.Unspecified] as null; if the
     * receiver is `Color.Unspecified`, it will be returned as-is.
     *
     * @param default the color to return when the receiver is null
     * @return the receiver if non-null, otherwise [default]
     */
    fun Color?.orDefault(default: Color = Color.Unspecified): Color =
        this ?: default

    /**
     * Returns a copy of this [Color] with its alpha multiplied by [multiplier].
     *
     * The RGB components remain unchanged. The resulting alpha is computed as:
     * `currentAlpha * multiplier`.
     *
     * This can be used to uniformly increase or decrease transparency while
     * preserving the original opacity proportion.
     *
     * @param multiplier factor applied to the current alpha value
     * @return a copy of this color with the adjusted alpha
     */
    fun Color.alphaMultiplier(multiplier: Float): Color =
        copy(alpha = alpha * multiplier)

    /**
     * Returns this [Color], reducing its alpha by half when [enabled] is false.
     *
     * If [enabled] is true, the color is returned unchanged.
     * If false, the resulting color keeps the same RGB components and
     * multiplies the current alpha by `0.5f`.
     *
     * @param enabled whether the color should remain fully effective
     * @return this color, or a version with its alpha halved when disabled
     */
    fun Color.semiTransparentIfDisabled(enabled: Boolean): Color =
        if (enabled) this else alphaMultiplier(0.5f)
}