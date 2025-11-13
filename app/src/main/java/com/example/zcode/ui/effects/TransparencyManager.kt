package com.example.zcode.ui.effects

import androidx.compose.ui.graphics.Color

/**
 * TransparencyManager - Manages transparency and alpha levels
 *
 * Provides utilities for handling transparency values and generating
 * colors with adjusted alpha channels
 */
object TransparencyManager {

    /**
     * Get color with adjusted alpha
     *
     * @param color The base color
     * @param alpha The alpha level (0.0-1.0)
     * @return Color with adjusted alpha
     */
    fun getColorWithAlpha(color: Color, alpha: Float): Color {
        val clampedAlpha = alpha.coerceIn(0f, 1f)
        return color.copy(alpha = clampedAlpha)
    }

    /**
     * Get transparent overlay color
     *
     * @param color The base color
     * @param transparency The transparency level (0.0-1.0)
     * @return Transparent color overlay
     */
    fun getTransparentOverlay(color: Color, transparency: Float): Color {
        val clampedTransparency = transparency.coerceIn(0f, 1f)
        return color.copy(alpha = clampedTransparency)
    }

    /**
     * Create a gradient with transparency
     *
     * @param startColor Starting color
     * @param endColor Ending color
     * @param transparency Transparency level
     * @return Pair of transparent colors
     */
    fun createTransparentGradient(
        startColor: Color,
        endColor: Color,
        transparency: Float
    ): Pair<Color, Color> {
        val clampedTransparency = transparency.coerceIn(0f, 1f)
        return Pair(
            startColor.copy(alpha = clampedTransparency),
            endColor.copy(alpha = clampedTransparency)
        )
    }

    /**
     * Blend two colors with transparency
     *
     * @param foreground Foreground color
     * @param background Background color
     * @param alpha Blend alpha (0.0-1.0)
     * @return Blended color
     */
    fun blendColors(
        foreground: Color,
        background: Color,
        alpha: Float
    ): Color {
        val clampedAlpha = alpha.coerceIn(0f, 1f)

        val r = foreground.red * clampedAlpha + background.red * (1 - clampedAlpha)
        val g = foreground.green * clampedAlpha + background.green * (1 - clampedAlpha)
        val b = foreground.blue * clampedAlpha + background.blue * (1 - clampedAlpha)

        return Color(r, g, b)
    }

    /**
     * Get transparency level from alpha value
     *
     * @param alpha Alpha value (0.0-1.0)
     * @return Transparency percentage (0-100)
     */
    fun alphaToTransparencyPercent(alpha: Float): Int {
        return ((1f - alpha.coerceIn(0f, 1f)) * 100).toInt()
    }

    /**
     * Get alpha value from transparency percentage
     *
     * @param percent Transparency percentage (0-100)
     * @return Alpha value (0.0-1.0)
     */
    fun transparencyPercentToAlpha(percent: Int): Float {
        return 1f - (percent.coerceIn(0, 100) / 100f)
    }
}

/**
 * Composition local for transparency level
 */
val LocalTransparencyLevel = androidx.compose.runtime.compositionLocalOf { 0.95f }

