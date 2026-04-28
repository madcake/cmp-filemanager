package ru.madcake.filemanager.core.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import kotlin.math.max
import kotlin.math.min

data class ValueFilterSettings(
    val contrastThreshold: Float = 0.5f,
    val brightnessThreshold: Float = 0.5f,
    val contrastBoost: Float = 1.5f,
    val showContrastSpots: Boolean = true,
    val highlightBrightAreas: Boolean = true,
    val highlightDarkAreas: Boolean = true,
    val edgeDetection: Boolean = false,
    val analysisMode: ValueAnalysisMode = ValueAnalysisMode.LUMINANCE_CONTRAST
)

enum class ValueAnalysisMode {
    LUMINANCE_CONTRAST,
    BRIGHTNESS_ZONES,
    EDGE_DETECTION,
    HIGH_CONTRAST_SPOTS
}

object ValueFilterProcessor {
    
    fun createValueFilterMatrix(settings: ValueFilterSettings): ColorMatrix {
        return when (settings.analysisMode) {
            ValueAnalysisMode.LUMINANCE_CONTRAST -> createLuminanceContrastFilter(settings)
            ValueAnalysisMode.BRIGHTNESS_ZONES -> createBrightnessZoneFilter(settings)
            ValueAnalysisMode.EDGE_DETECTION -> createEdgeDetectionFilter(settings)
            ValueAnalysisMode.HIGH_CONTRAST_SPOTS -> createHighContrastSpotFilter(settings)
        }
    }
    
    private fun createLuminanceContrastFilter(settings: ValueFilterSettings): ColorMatrix {
        // Convert to grayscale and apply extreme contrast for posterization effect
        val contrast = settings.contrastBoost * 2f
        val threshold = settings.brightnessThreshold
        
        // Extreme brightness adjustment to create distinct light/dark zones
        val brightnessOffset = (threshold - 0.5f) * 200f
        
        // Standard luminance coefficients (ITU-R BT.709)
        val luma = 0.2126f
        val lumaG = 0.7152f  
        val lumaB = 0.0722f
        
        return ColorMatrix(
            floatArrayOf(
                luma * contrast, lumaG * contrast, lumaB * contrast, 0f, brightnessOffset,
                luma * contrast, lumaG * contrast, lumaB * contrast, 0f, brightnessOffset,
                luma * contrast, lumaG * contrast, lumaB * contrast, 0f, brightnessOffset,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }
    
    private fun createBrightnessZoneFilter(settings: ValueFilterSettings): ColorMatrix {
        // Extreme posterization to create distinct bright/dark zones like the target image
        val threshold = settings.brightnessThreshold
        val contrast = settings.contrastBoost * 4f // Much higher contrast
        
        // Extreme brightness offset for posterization effect
        val brightnessOffset = (threshold - 0.5f) * 255f
        
        return ColorMatrix(
            floatArrayOf(
                0.299f * contrast, 0.587f * contrast, 0.114f * contrast, 0f, brightnessOffset,
                0.299f * contrast, 0.587f * contrast, 0.114f * contrast, 0f, brightnessOffset,
                0.299f * contrast, 0.587f * contrast, 0.114f * contrast, 0f, brightnessOffset,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }
    
    private fun createEdgeDetectionFilter(settings: ValueFilterSettings): ColorMatrix {
        // High contrast grayscale for edge detection
        val edgeStrength = settings.contrastBoost * 3f
        
        return ColorMatrix(
            floatArrayOf(
                0.299f * edgeStrength, 0.587f * edgeStrength, 0.114f * edgeStrength, 0f, -128f,
                0.299f * edgeStrength, 0.587f * edgeStrength, 0.114f * edgeStrength, 0f, -128f,
                0.299f * edgeStrength, 0.587f * edgeStrength, 0.114f * edgeStrength, 0f, -128f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }
    
    private fun createHighContrastSpotFilter(settings: ValueFilterSettings): ColorMatrix {
        // Maximum contrast posterization like the target image
        val spotContrast = settings.contrastBoost * 6f // Maximum contrast
        val threshold = settings.contrastThreshold
        
        // Extreme brightness clamping for pure black/white zones
        val brightnessClamp = (threshold - 0.5f) * 300f
        
        return ColorMatrix(
            floatArrayOf(
                0.299f * spotContrast, 0.587f * spotContrast, 0.114f * spotContrast, 0f, brightnessClamp,
                0.299f * spotContrast, 0.587f * spotContrast, 0.114f * spotContrast, 0f, brightnessClamp,
                0.299f * spotContrast, 0.587f * spotContrast, 0.114f * spotContrast, 0f, brightnessClamp,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }
    
    fun createContrastAnalysisFilter(
        contrastLevel: Float,
        brightnessThreshold: Float,
        spotDetection: Boolean = false
    ): ColorMatrix {
        // Enhanced contrast analysis for light/dark spot detection
        val contrast = contrastLevel * 2f
        val brightness = (brightnessThreshold - 0.5f) * 0.4f
        val spotBoost = if (spotDetection) 1.5f else 1f
        
        return ColorMatrix(
            floatArrayOf(
                0.299f * contrast * spotBoost, 0.587f * contrast * spotBoost, 0.114f * contrast * spotBoost, 0f, brightness * 255f,
                0.299f * contrast * spotBoost, 0.587f * contrast * spotBoost, 0.114f * contrast * spotBoost, 0f, brightness * 255f,
                0.299f * contrast * spotBoost, 0.587f * contrast * spotBoost, 0.114f * contrast * spotBoost, 0f, brightness * 255f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }
    
    fun createLuminanceMapFilter(
        sensitivity: Float,
        highlightThreshold: Float
    ): ColorMatrix {
        // Creates a luminance map for identifying light/dark regions
        val luminanceBoost = sensitivity * 3f
        val highlight = (highlightThreshold - 0.5f) * 200f
        
        return ColorMatrix(
            floatArrayOf(
                0.2126f * luminanceBoost, 0.7152f * luminanceBoost, 0.0722f * luminanceBoost, 0f, highlight,
                0.2126f * luminanceBoost, 0.7152f * luminanceBoost, 0.0722f * luminanceBoost, 0f, highlight,
                0.2126f * luminanceBoost, 0.7152f * luminanceBoost, 0.0722f * luminanceBoost, 0f, highlight,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }
    
    // Luminance analysis utility functions
    fun calculateLuminance(r: Float, g: Float, b: Float): Float {
        // Standard ITU-R BT.709 luminance calculation
        return 0.2126f * r + 0.7152f * g + 0.0722f * b
    }
    
    fun calculateContrast(luminance1: Float, luminance2: Float): Float {
        // WCAG contrast ratio calculation
        val lighter = max(luminance1, luminance2)
        val darker = min(luminance1, luminance2)
        return (lighter + 0.05f) / (darker + 0.05f)
    }
    
    fun isHighContrastSpot(color: Color, threshold: Float): Boolean {
        val luminance = calculateLuminance(color.red, color.green, color.blue)
        return luminance > (1f - threshold) || luminance < threshold
    }
    
    fun getBrightnessCategory(color: Color, threshold: Float): BrightnessCategory {
        val luminance = calculateLuminance(color.red, color.green, color.blue)
        return when {
            luminance > (threshold + 0.3f) -> BrightnessCategory.VERY_BRIGHT
            luminance > threshold -> BrightnessCategory.BRIGHT
            luminance > (threshold - 0.3f) -> BrightnessCategory.MEDIUM
            luminance > (threshold - 0.6f) -> BrightnessCategory.DARK
            else -> BrightnessCategory.VERY_DARK
        }
    }
    
    enum class BrightnessCategory {
        VERY_BRIGHT, BRIGHT, MEDIUM, DARK, VERY_DARK
    }
    
    // Predefined contrast analysis presets
    object Presets {
        val STANDARD_CONTRAST = ValueFilterSettings(
            contrastThreshold = 0.5f,
            brightnessThreshold = 0.5f,
            contrastBoost = 2.5f, // Increased for more aggressive contrast
            analysisMode = ValueAnalysisMode.LUMINANCE_CONTRAST
        )
        
        val HIGH_CONTRAST_SPOTS = ValueFilterSettings(
            contrastThreshold = 0.3f,
            brightnessThreshold = 0.5f,
            contrastBoost = 3.5f, // Increased for posterization effect
            showContrastSpots = true,
            analysisMode = ValueAnalysisMode.HIGH_CONTRAST_SPOTS
        )
        
        val BRIGHTNESS_ZONES = ValueFilterSettings(
            contrastThreshold = 0.4f,
            brightnessThreshold = 0.6f,
            contrastBoost = 3f, // Increased for distinct zones
            highlightBrightAreas = true,
            highlightDarkAreas = true,
            analysisMode = ValueAnalysisMode.BRIGHTNESS_ZONES
        )
        
        val EDGE_DETECTION = ValueFilterSettings(
            contrastThreshold = 0.7f,
            brightnessThreshold = 0.5f,
            contrastBoost = 3f,
            edgeDetection = true,
            analysisMode = ValueAnalysisMode.EDGE_DETECTION
        )
        
        val DARK_AREA_ANALYSIS = ValueFilterSettings(
            contrastThreshold = 0.3f,
            brightnessThreshold = 0.3f,
            contrastBoost = 2f,
            highlightDarkAreas = true,
            analysisMode = ValueAnalysisMode.LUMINANCE_CONTRAST
        )
        
        val BRIGHT_AREA_ANALYSIS = ValueFilterSettings(
            contrastThreshold = 0.7f,
            brightnessThreshold = 0.7f,
            contrastBoost = 2f,
            highlightBrightAreas = true,
            analysisMode = ValueAnalysisMode.LUMINANCE_CONTRAST
        )
        
        val ALL_PRESETS = listOf(
            "Standard Contrast" to STANDARD_CONTRAST,
            "High Contrast Spots" to HIGH_CONTRAST_SPOTS,
            "Brightness Zones" to BRIGHTNESS_ZONES,
            "Edge Detection" to EDGE_DETECTION,
            "Dark Area Analysis" to DARK_AREA_ANALYSIS,
            "Bright Area Analysis" to BRIGHT_AREA_ANALYSIS
        )
    }
}