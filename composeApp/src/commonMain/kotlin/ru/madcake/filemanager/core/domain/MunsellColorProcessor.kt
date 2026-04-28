package ru.madcake.filemanager.core.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import kotlin.math.*

/**
 * Munsell Color Processor
 * Implements conversion between RGB and Munsell color space (HVC)
 * Based on the Munsell color system where:
 * - Hue: 0-100 (color wheel position)
 * - Value: 0-10 (lightness, 0=black, 10=white)
 * - Chroma: 0-20+ (color intensity/saturation)
 */
object MunsellColorProcessor {

    /**
     * Munsell color data class
     */
    data class MunsellColor(
        val hue: Float,        // 0-100 (0 and 100 both = 10RP)
        val value: Float,      // 0-10 (0=black, 10=white)
        val chroma: Float      // 0-20+ (0=gray, higher=more vivid)
    )

    /**
     * Convert RGB to approximate Munsell HVC coordinates
     * Uses simplified conversion based on perceptual color theory
     */
    fun rgbToMunsell(r: Float, g: Float, b: Float): MunsellColor {
        // Convert to 0-1 range
        val red = r.coerceIn(0f, 1f)
        val green = g.coerceIn(0f, 1f)
        val blue = b.coerceIn(0f, 1f)
        
        // Calculate Munsell Value (perceptual lightness)
        val value = calculateMunsellValue(red, green, blue)
        
        // Calculate Hue (simplified color wheel mapping)
        val hue = calculateMunsellHue(red, green, blue)
        
        // Calculate Chroma (color intensity)
        val chroma = calculateMunsellChroma(red, green, blue, value)
        
        return MunsellColor(hue, value, chroma)
    }

    /**
     * Convert Munsell HVC to approximate RGB coordinates
     * Uses simplified conversion for real-time processing
     */
    fun munsellToRGB(munsell: MunsellColor): Color {
        val hue = munsell.hue.coerceIn(0f, 100f)
        val value = munsell.value.coerceIn(0f, 10f)
        val chroma = munsell.chroma.coerceIn(0f, 20f)
        
        // Convert Value to lightness (0-1)
        val lightness = value / 10f
        
        // Convert Hue to angle (0-360 degrees)
        val hueAngle = (hue * 3.6f) % 360f
        
        // Convert Chroma to saturation (0-1)
        val saturation = (chroma / 20f).coerceIn(0f, 1f)
        
        // Convert HSL-like coordinates to RGB
        return hslToRgb(hueAngle, saturation, lightness)
    }

    /**
     * Calculate Munsell Value (perceptual lightness)
     * Based on human visual perception weights
     */
    private fun calculateMunsellValue(r: Float, g: Float, b: Float): Float {
        // Munsell value weights (different from standard RGB luminance)
        val luminance = 0.212f * r + 0.715f * g + 0.073f * b
        // Convert to Munsell Value scale (0-10)
        return sqrt(luminance) * 10f
    }

    /**
     * Calculate Munsell Hue (0-100 scale)
     * Maps RGB to Munsell hue notation
     */
    private fun calculateMunsellHue(r: Float, g: Float, b: Float): Float {
        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val delta = max - min
        
        if (delta == 0f) return 0f // Gray has no hue
        
        val hue = when (max) {
            r -> ((g - b) / delta) % 6f
            g -> (b - r) / delta + 2f
            b -> (r - g) / delta + 4f
            else -> 0f
        }
        
        // Convert to Munsell hue scale (0-100)
        return ((hue * 60f + 360f) % 360f) * 100f / 360f
    }

    /**
     * Calculate Munsell Chroma (color intensity)
     * Based on color saturation and value
     */
    private fun calculateMunsellChroma(r: Float, g: Float, b: Float, value: Float): Float {
        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val delta = max - min
        
        if (max == 0f || value == 0f) return 0f
        
        // Simplified chroma calculation
        val saturation = delta / max
        return saturation * (value / 10f) * 20f
    }

    /**
     * Convert HSL to RGB
     * Helper function for Munsell to RGB conversion
     */
    private fun hslToRgb(h: Float, s: Float, l: Float): Color {
        val hue = h / 60f
        val chroma = (1f - abs(2f * l - 1f)) * s
        val x = chroma * (1f - abs((hue % 2f) - 1f))
        val m = l - chroma / 2f
        
        val (r1, g1, b1) = when (hue.toInt()) {
            0 -> Triple(chroma, x, 0f)
            1 -> Triple(x, chroma, 0f)
            2 -> Triple(0f, chroma, x)
            3 -> Triple(0f, x, chroma)
            4 -> Triple(x, 0f, chroma)
            5 -> Triple(chroma, 0f, x)
            else -> Triple(0f, 0f, 0f)
        }
        
        return Color(
            red = (r1 + m).coerceIn(0f, 1f),
            green = (g1 + m).coerceIn(0f, 1f),
            blue = (b1 + m).coerceIn(0f, 1f)
        )
    }

    /**
     * Create ColorMatrix for Munsell color space transformation
     */
    fun createMunsellColorMatrix(
        hueShift: Float = 0f,        // -50 to +50 Munsell hue units
        valueAdjustment: Float = 0f, // -5 to +5 Munsell value units
        chromaMultiplier: Float = 1f // 0.1 to 3.0 chroma multiplier
    ): ColorMatrix {
        // This is a simplified approach using ColorMatrix
        // For precise Munsell conversion, pixel-by-pixel processing would be needed
        
        val hueRadians = (hueShift * 3.6f) * PI / 180f
        val cosHue = cos(hueRadians).toFloat()
        val sinHue = sin(hueRadians).toFloat()
        
        val valueOffset = valueAdjustment * 25.5f // Convert to 0-255 range
        val chromaFactor = chromaMultiplier
        
        return ColorMatrix(
            floatArrayOf(
                cosHue * chromaFactor + 0.213f, sinHue * chromaFactor + 0.715f, -sinHue * chromaFactor + 0.072f, 0f, valueOffset,
                -sinHue * chromaFactor + 0.213f, cosHue * chromaFactor + 0.715f, sinHue * chromaFactor + 0.072f, 0f, valueOffset,
                sinHue * chromaFactor + 0.213f, -sinHue * chromaFactor + 0.715f, cosHue * chromaFactor + 0.072f, 0f, valueOffset,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    /**
     * Predefined Munsell colors for reference
     */
    object StandardColors {
        val MUNSELL_RED = MunsellColor(hue = 0f, value = 5f, chroma = 10f)
        val MUNSELL_YELLOW = MunsellColor(hue = 25f, value = 8f, chroma = 12f)
        val MUNSELL_GREEN = MunsellColor(hue = 50f, value = 6f, chroma = 8f)
        val MUNSELL_BLUE = MunsellColor(hue = 75f, value = 4f, chroma = 6f)
        val MUNSELL_PURPLE = MunsellColor(hue = 87.5f, value = 3f, chroma = 8f)
        val MUNSELL_GRAY = MunsellColor(hue = 0f, value = 5f, chroma = 0f)
        val MUNSELL_WHITE = MunsellColor(hue = 0f, value = 9.5f, chroma = 0f)
        val MUNSELL_BLACK = MunsellColor(hue = 0f, value = 1f, chroma = 0f)
    }
}