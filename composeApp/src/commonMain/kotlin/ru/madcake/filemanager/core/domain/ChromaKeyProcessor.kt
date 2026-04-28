package ru.madcake.filemanager.core.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

data class ChromaKeySettings(
    val targetColor: Color = Color.Green,
    val tolerance: Float = 0.3f,
    val softness: Float = 0.1f,
    val spillSuppression: Float = 0.5f,
    val featherRadius: Float = 2.0f,
    val edgeSmoothing: Boolean = true,
    val colorSpaceMode: ChromaKeyColorSpace = ChromaKeyColorSpace.HSL
)

enum class ChromaKeyColorSpace {
    RGB, HSL, YUV
}

object ChromaKeyProcessor {
    
    fun createChromaKeyMatrix(settings: ChromaKeySettings): ColorMatrix {
        return when (settings.colorSpaceMode) {
            ChromaKeyColorSpace.RGB -> createRGBChromaKey(settings)
            ChromaKeyColorSpace.HSL -> createHSLChromaKey(settings)
            ChromaKeyColorSpace.YUV -> createYUVChromaKey(settings)
        }
    }
    
    private fun createRGBChromaKey(settings: ChromaKeySettings): ColorMatrix {
        val target = settings.targetColor
        val tolerance = settings.tolerance
        val spillSuppression = settings.spillSuppression
        
        // Advanced RGB chroma key matrix with spill suppression
        return ColorMatrix(
            floatArrayOf(
                1f - spillSuppression * target.red, -spillSuppression * target.green, -spillSuppression * target.blue, 0f, 0f,
                -spillSuppression * target.red, 1f - spillSuppression * target.green, -spillSuppression * target.blue, 0f, 0f,
                -spillSuppression * target.red, -spillSuppression * target.green, 1f - spillSuppression * target.blue, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }
    
    private fun createHSLChromaKey(settings: ChromaKeySettings): ColorMatrix {
        val target = settings.targetColor
        val tolerance = settings.tolerance
        
        // Convert target color to HSL for better chroma key precision
        val hsl = rgbToHsl(target.red, target.green, target.blue)
        val targetHue = hsl[0]
        
        // HSL-based chroma key matrix focusing on hue separation
        return ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, 0f,
                0f, 1f - tolerance, 0f, 0f, 0f,
                0f, 0f, 1f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }
    
    private fun createYUVChromaKey(settings: ChromaKeySettings): ColorMatrix {
        val tolerance = settings.tolerance
        val spillSuppression = settings.spillSuppression
        
        // YUV color space chroma key - focuses on chrominance channels
        return ColorMatrix(
            floatArrayOf(
                0.299f, 0.587f, 0.114f, 0f, 0f,
                -0.147f * (1f - tolerance), -0.289f * (1f - tolerance), 0.436f * (1f - tolerance), 0f, 0f,
                0.615f * (1f - tolerance), -0.515f * (1f - tolerance), -0.100f * (1f - tolerance), 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }
    
    fun createFeatheredChromaKey(settings: ChromaKeySettings): ColorMatrix {
        val baseMatrix = createChromaKeyMatrix(settings)
        
        if (!settings.edgeSmoothing) return baseMatrix
        
        // Apply edge feathering through matrix modification
        val featherStrength = settings.featherRadius / 10f
        val smoothMatrix = ColorMatrix(
            floatArrayOf(
                1f - featherStrength, featherStrength * 0.5f, featherStrength * 0.5f, 0f, 0f,
                featherStrength * 0.5f, 1f - featherStrength, featherStrength * 0.5f, 0f, 0f,
                featherStrength * 0.5f, featherStrength * 0.5f, 1f - featherStrength, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        
        baseMatrix.timesAssign(smoothMatrix)
        return baseMatrix
    }
    
    fun calculateColorDistance(color1: Color, color2: Color, colorSpace: ChromaKeyColorSpace): Float {
        return when (colorSpace) {
            ChromaKeyColorSpace.RGB -> {
                val dr = color1.red - color2.red
                val dg = color1.green - color2.green
                val db = color1.blue - color2.blue
                sqrt(dr * dr + dg * dg + db * db)
            }
            ChromaKeyColorSpace.HSL -> {
                val hsl1 = rgbToHsl(color1.red, color1.green, color1.blue)
                val hsl2 = rgbToHsl(color2.red, color2.green, color2.blue)
                abs(hsl1[0] - hsl2[0]) / 360f // Hue distance
            }
            ChromaKeyColorSpace.YUV -> {
                val yuv1 = rgbToYuv(color1.red, color1.green, color1.blue)
                val yuv2 = rgbToYuv(color2.red, color2.green, color2.blue)
                val du = yuv1[1] - yuv2[1]
                val dv = yuv1[2] - yuv2[2]
                sqrt(du * du + dv * dv)
            }
        }
    }
    
    private fun rgbToHsl(r: Float, g: Float, b: Float): FloatArray {
        val max = max(max(r, g), b)
        val min = min(min(r, g), b)
        val delta = max - min
        
        val h = when {
            delta == 0f -> 0f
            max == r -> 60f * (((g - b) / delta) % 6f)
            max == g -> 60f * ((b - r) / delta + 2f)
            else -> 60f * ((r - g) / delta + 4f)
        }
        
        val l = (max + min) / 2f
        val s = if (delta == 0f) 0f else delta / (1f - abs(2f * l - 1f))
        
        return floatArrayOf(h, s, l)
    }
    
    private fun rgbToYuv(r: Float, g: Float, b: Float): FloatArray {
        val y = 0.299f * r + 0.587f * g + 0.114f * b
        val u = -0.147f * r - 0.289f * g + 0.436f * b
        val v = 0.615f * r - 0.515f * g - 0.100f * b
        return floatArrayOf(y, u, v)
    }
    
    // Predefined common chroma key colors
    object PresetColors {
        val CHROMA_GREEN = Color(0xFF00FF00)
        val CHROMA_BLUE = Color(0xFF0000FF) 
        val CHROMA_RED = Color(0xFFFF0000)
        val CHROMA_MAGENTA = Color(0xFFFF00FF)
        val CHROMA_CYAN = Color(0xFF00FFFF)
        val CHROMA_YELLOW = Color(0xFFFFFF00)
        
        val ALL_PRESETS = listOf(
            "Chroma Green" to CHROMA_GREEN,
            "Chroma Blue" to CHROMA_BLUE,
            "Chroma Red" to CHROMA_RED,
            "Magenta" to CHROMA_MAGENTA,
            "Cyan" to CHROMA_CYAN,
            "Yellow" to CHROMA_YELLOW
        )
    }
}