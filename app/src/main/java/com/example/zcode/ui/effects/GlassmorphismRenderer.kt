package com.example.zcode.ui.effects

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * GlassmorphismRenderer - Handles glassmorphism visual effects
 *
 * Implements frosted glass effect rendering with:
 * - Blur background
 * - Semi-transparent overlay
 * - Smooth edges
 * - Layered depth effect
 */
object GlassmorphismRenderer {

    /**
     * Create glassmorphism modifier
     *
     * @param blurRadius Blur radius for the glass effect
     * @param alpha Transparency of the glass
     * @param cornerRadius Corner radius of the glass panel
     * @return Modifier with glassmorphism effect applied
     */
    @Composable
    fun glassmorphismModifier(
        blurRadius: Dp = 20.dp,
        alpha: Float = 0.8f,
        cornerRadius: Dp = 16.dp
    ): Modifier {
        return Modifier
            .blur(radius = blurRadius)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = alpha),
                shape = RoundedCornerShape(cornerRadius)
            )
            .graphicsLayer {
                this.alpha = alpha
            }
    }

    /**
     * Create glass panel with frosted effect
     *
     * @param blurRadius Blur radius
     * @param alpha Alpha/transparency level
     * @param cornerRadius Corner radius
     * @param backgroundColor Base background color
     * @return Modifier for glass panel
     */
    @Composable
    fun glassPanel(
        blurRadius: Dp = 12.dp,
        alpha: Float = 0.85f,
        cornerRadius: Dp = 12.dp,
        backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant
    ): Modifier {
        return Modifier
            .background(
                color = backgroundColor.copy(alpha = alpha),
                shape = RoundedCornerShape(cornerRadius)
            )
            .graphicsLayer {
                this.alpha = alpha
                compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
            }
    }

    /**
     * Create layered glass effect for depth
     *
     * @param depth Number of layers (1-3)
     * @param blurRadius Blur radius
     * @param baseAlpha Base alpha value
     * @return List of modifiers for layered effect
     */
    @Composable
    fun layeredGlassEffect(
        depth: Int = 2,
        blurRadius: Dp = 16.dp,
        baseAlpha: Float = 0.8f
    ): List<Modifier> {
        val clampedDepth = depth.coerceIn(1, 3)

        return (0 until clampedDepth).map { layer ->
            val alphaDecrement = 0.1f * (layer + 1)
            val currentAlpha = baseAlpha - alphaDecrement

            Modifier
                .blur(radius = blurRadius * (1f + layer * 0.1f))
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(
                        alpha = currentAlpha.coerceIn(0f, 1f)
                    ),
                    shape = RoundedCornerShape((12 + layer * 2).dp)
                )
        }
    }

    /**
     * Create glassmorphism surface colors
     *
     * @param baseColor Base color
     * @param transparency Transparency level (0.0-1.0)
     * @return Glass effect color
     */
    fun getGlassColor(
        baseColor: Color,
        transparency: Float = 0.85f
    ): Color {
        return baseColor.copy(alpha = transparency.coerceIn(0f, 1f))
    }

    /**
     * Get blur radius from intensity
     *
     * @param intensity Blur intensity (0-20)
     * @return Blur radius in Dp
     */
    fun getBlurRadiusFromIntensity(intensity: Float): Dp {
        val clampedIntensity = intensity.coerceIn(0f, 20f)
        return (clampedIntensity + 5).dp  // Maps 0-20 to 5-25 dp
    }

    /**
     * Calculate optimal alpha for glassmorphism based on blur
     *
     * @param blurRadius Blur radius in Dp
     * @return Recommended alpha value
     */
    fun calculateOptimalAlpha(blurRadius: Dp): Float {
        val blurValue = blurRadius.value
        return when {
            blurValue < 10f -> 0.7f
            blurValue < 15f -> 0.8f
            blurValue < 20f -> 0.85f
            else -> 0.9f
        }
    }
}

/**
 * GlassmorphismTheme - Composable for applying glassmorphism globally
 *
 * Wraps content with glassmorphism effects
 */
@Composable
fun GlassmorphismTheme(
    enabled: Boolean = true,
    blurIntensity: Float = 10f,
    transparency: Float = 0.9f,
    content: @Composable () -> Unit
) {
    if (enabled) {
        val blurRadius = GlassmorphismRenderer.getBlurRadiusFromIntensity(blurIntensity)

        androidx.compose.material3.Surface(
            modifier = GlassmorphismRenderer.glassPanel(
                blurRadius = blurRadius,
                alpha = transparency
            )
        ) {
            content()
        }
    } else {
        content()
    }
}

