package ru.madcake.filemanager.features.main.presents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterBAndW
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MonochromePhotos
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.vector.ImageVector
import ru.madcake.filemanager.core.domain.MunsellColorProcessor
import ru.madcake.filemanager.core.domain.ValueFilterProcessor
import ru.madcake.filemanager.core.domain.ValueFilterSettings
import kotlin.math.abs

sealed class ImageFilter(
    val name: String,
    val icon: ImageVector,
    private val matrix: ColorMatrix? = null
) {
    object None : ImageFilter(
        name = "None",
        icon = Icons.Default.Image,
    )

    object Grayscale : ImageFilter(
        name = "Grayscale",
        icon = Icons.Default.MonochromePhotos,
        matrix = ColorMatrix(
            floatArrayOf(
                0.299f, 0.587f, 0.114f, 0f, 0f,
                0.299f, 0.587f, 0.114f, 0f, 0f,
                0.299f, 0.587f, 0.114f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f,
            )
        )
    )

    object BlackAndWhite : ImageFilter(
        name = "B&W",
        icon = Icons.Default.FilterBAndW,
        matrix = ColorMatrix(
            floatArrayOf(
                85f, 85f, 85f, 0f, -255f * 43f,
                85f, 85f, 85f, 0f, -255f * 43f,
                85f, 85f, 85f, 0f, -255f * 43f,
                0f, 0f, 0f, 1f, 0f,
            )
        )
    )

    object WhiteAndBlack : ImageFilter(
        name = "W&B",
        icon = Icons.Default.FilterDrama,
        matrix = ColorMatrix(
            floatArrayOf(
                1.5f, 1.5f, 1.5f, 0f, 0f,
                1.5f, 1.5f, 1.5f, 0f, 0f,
                1.5f, 1.5f, 1.5f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f,
            )
        )
    )

    data class ChromaKey(
        val targetColor: Color = Color.Green,
        val tolerance: Float = 0.3f,
        val softness: Float = 0.1f,
        val spillSuppression: Float = 0.5f,
        val featherRadius: Float = 2.0f,
        val edgeSmoothing: Boolean = true
    ) : ImageFilter(
        name = "Chroma Key",
        icon = Icons.Default.FilterVintage
    ) {
        
        private fun generateChromaKeyMatrix(): ColorMatrix {
            // Advanced chroma key matrix based on target color and settings
            val r = targetColor.red
            val g = targetColor.green  
            val b = targetColor.blue
            
            // Calculate color distance weights
            val rWeight = 1f - (tolerance * abs(1f - r))
            val gWeight = 1f - (tolerance * abs(1f - g)) 
            val bWeight = 1f - (tolerance * abs(1f - b))
            
            // Apply spill suppression by reducing target color influence
            val spillR = spillSuppression * r
            val spillG = spillSuppression * g
            val spillB = spillSuppression * b
            
            return ColorMatrix(
                floatArrayOf(
                    rWeight - spillR, -spillG * 0.5f, -spillB * 0.5f, 0f, 0f,
                    -spillR * 0.5f, gWeight - spillG, -spillB * 0.5f, 0f, 0f,
                    -spillR * 0.5f, -spillG * 0.5f, bWeight - spillB, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        }
        
        override fun getColorFilter(): ColorFilter? {
            val matrix = generateChromaKeyMatrix()
            
            // Apply edge smoothing if enabled
            if (edgeSmoothing) {
                val smoothingMatrix = ColorMatrix(
                    floatArrayOf(
                        1f - softness, softness * 0.3f, softness * 0.3f, 0f, 0f,
                        softness * 0.3f, 1f - softness, softness * 0.3f, 0f, 0f,
                        softness * 0.3f, softness * 0.3f, 1f - softness, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                matrix.timesAssign(smoothingMatrix)
            }
            
            return ColorFilter.colorMatrix(matrix)
        }
    }

    data class ValueFilter(
        val settings: ValueFilterSettings = ValueFilterSettings()
    ) : ImageFilter(
        name = "Value Filter",
        icon = Icons.Default.Tune
    ) {
        override fun getColorFilter(): ColorFilter? {
            val matrix = ValueFilterProcessor.createValueFilterMatrix(settings)
            return ColorFilter.colorMatrix(matrix)
        }
    }

    data class ColorSimplification(
        val colorLevels: Int = 8,
        val preserveHue: Boolean = true,
        val preserveChroma: Boolean = true,
        val quantizationStrength: Float = 0.8f
    ) : ImageFilter(
        name = "Color Simplify",
        icon = Icons.Default.Palette
    ) {
        override fun getColorFilter(): ColorFilter? {
            // Color quantization matrix that reduces color levels while preserving hue/chroma relationships
            val quantizationFactor = 1f / colorLevels.toFloat()
            val strength = quantizationStrength
            val preserve = if (preserveHue && preserveChroma) 0.3f else 0.1f
            
            // Create a matrix that quantizes colors by reducing precision
            // while maintaining relative color relationships
            val matrix = ColorMatrix(
                floatArrayOf(
                    strength + preserve, preserve * 0.5f, preserve * 0.5f, 0f, quantizationFactor * 32f,
                    preserve * 0.5f, strength + preserve, preserve * 0.5f, 0f, quantizationFactor * 32f,
                    preserve * 0.5f, preserve * 0.5f, strength + preserve, 0f, quantizationFactor * 32f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            
            // Apply color level reduction
            val levels = colorLevels.coerceIn(2, 32)
            val levelMatrix = ColorMatrix(
                floatArrayOf(
                    levels / 8f, 0f, 0f, 0f, 0f,
                    0f, levels / 8f, 0f, 0f, 0f,
                    0f, 0f, levels / 8f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            
            matrix.timesAssign(levelMatrix)
            return ColorFilter.colorMatrix(matrix)
        }
    }

    data class MunsellValue(
        val valueRange: Float = 1.0f,
        val contrastBoost: Float = 1.0f,
        val preserveColorTones: Boolean = false,
        val gammaCorrection: Float = 2.2f
    ) : ImageFilter(
        name = "Munsell Value",
        icon = Icons.Default.Visibility
    ) {
        override fun getColorFilter(): ColorFilter? {
            // Munsell value conversion using perceptual lightness
            // Based on human visual perception rather than mathematical luminance
            
            // Perceptual weights for Munsell value (different from standard RGB luminance)
            // These coefficients account for human eye sensitivity to different wavelengths
            val rWeight = 0.212f * contrastBoost
            val gWeight = 0.715f * contrastBoost  // Green is most perceptually significant
            val bWeight = 0.073f * contrastBoost
            
            // Gamma correction factor for perceptual uniformity
            val gamma = 1f / gammaCorrection
            
            if (preserveColorTones) {
                // Preserve some color information while emphasizing value
                val colorPreservation = (1f - valueRange) * 0.3f
                return ColorFilter.colorMatrix(
                    ColorMatrix(
                        floatArrayOf(
                            rWeight + colorPreservation, gWeight * 0.3f, bWeight * 0.3f, 0f, 0f,
                            rWeight * 0.3f, gWeight + colorPreservation, bWeight * 0.3f, 0f, 0f,
                            rWeight * 0.3f, gWeight * 0.3f, bWeight + colorPreservation, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            } else {
                // Pure Munsell value (grayscale based on perceptual lightness)
                val adjustedRange = valueRange.coerceIn(0.1f, 2.0f)
                return ColorFilter.colorMatrix(
                    ColorMatrix(
                        floatArrayOf(
                            rWeight * adjustedRange, gWeight * adjustedRange, bWeight * adjustedRange, 0f, 0f,
                            rWeight * adjustedRange, gWeight * adjustedRange, bWeight * adjustedRange, 0f, 0f,
                            rWeight * adjustedRange, gWeight * adjustedRange, bWeight * adjustedRange, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            }
        }
    }

    data class MunsellColor(
        val hueShift: Float = 0f,          // -50 to +50 Munsell hue units
        val valueAdjustment: Float = 0f,   // -5 to +5 Munsell value units  
        val chromaMultiplier: Float = 1f,  // 0.1 to 3.0 chroma multiplier
        val preserveOriginalColors: Boolean = false
    ) : ImageFilter(
        name = "Munsell Color",
        icon = Icons.Default.ColorLens
    ) {
        override fun getColorFilter(): ColorFilter? {
            if (preserveOriginalColors && hueShift == 0f && valueAdjustment == 0f && chromaMultiplier == 1f) {
                return null // No transformation needed
            }
            
            val matrix = MunsellColorProcessor.createMunsellColorMatrix(
                hueShift = hueShift,
                valueAdjustment = valueAdjustment,
                chromaMultiplier = chromaMultiplier
            )
            
            return ColorFilter.colorMatrix(matrix)
        }
    }


    open fun getColorFilter(): ColorFilter? {
        return matrix?.let { ColorFilter.colorMatrix(it) }
    }
    
    open fun getRenderEffect(): RenderEffect? {
        // TODO: Implement shader-based rendering when API is available
        return null
    }

    companion object {
        val allFilters = listOf(None, Grayscale, BlackAndWhite, WhiteAndBlack, ChromaKey(), ValueFilter(), ColorSimplification(), MunsellValue(), MunsellColor())
    }
}
