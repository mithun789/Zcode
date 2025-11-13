package com.example.zcode.ui.effects

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur

/**
 * BlurFilter - Handles blur effect rendering
 *
 * Uses RenderScript for efficient blur processing on bitmaps
 * Supports blur radius from 1 to 25
 */
class BlurFilter(private val context: Context) {

    private var renderScript: RenderScript? = null

    /**
     * Apply blur effect to bitmap
     *
     * @param bitmap The bitmap to blur
     * @param radius The blur radius (1-25)
     * @return Blurred bitmap
     * @throws IllegalArgumentException if radius is out of range
     */
    fun applyBlur(bitmap: Bitmap, radius: Float): Bitmap {
        if (radius < 1f || radius > 25f) {
            throw IllegalArgumentException("Blur radius must be between 1 and 25, got $radius")
        }

        return try {
            // Create a copy of the bitmap to avoid modifying the original
            val output = bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)

            // Initialize RenderScript if not already done
            if (renderScript == null) {
                renderScript = RenderScript.create(context)
            }

            val rs = renderScript ?: return bitmap

            // Create allocations for input and output
            val allocationIn = Allocation.createFromBitmap(rs, bitmap)
            val allocationOut = Allocation.createFromBitmap(rs, output)

            // Create blur script
            val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            blurScript.setRadius(radius)
            blurScript.setInput(allocationIn)

            // Execute blur
            blurScript.forEach(allocationOut)

            // Copy result back to output bitmap
            allocationOut.copyTo(output)

            // Cleanup
            allocationIn.destroy()
            allocationOut.destroy()
            blurScript.destroy()

            output
        } catch (e: Exception) {
            throw BlurFilterException("Failed to apply blur filter: ${e.message}", e)
        }
    }

    /**
     * Release RenderScript resources
     */
    fun release() {
        renderScript?.destroy()
        renderScript = null
    }

    /**
     * Get blur radius from intensity value (0-20dp)
     *
     * @param intensity Intensity value 0-20
     * @return Blur radius 1-25
     */
    companion object {
        fun intensityToRadius(intensity: Float): Float {
            return (intensity / 20f * 24f) + 1f  // Maps 0-20 to 1-25
        }
    }
}

/**
 * BlurFilterException - Custom exception for blur filter operations
 */
class BlurFilterException(message: String, cause: Throwable? = null) : Exception(message, cause)

