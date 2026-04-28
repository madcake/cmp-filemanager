package ru.madcake.filemanager.core.domain

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toPixelMap
import coil3.Bitmap
import coil3.transform.Transformation

/**
 * Coil transformation for MunsellDirect filter
 * Applies pixel-by-pixel Munsell color space transformation
 */
class MunsellBitmapTransform(
    private val fileName: String,
    private val settings: MunsellDirectProcessor.ProcessingSettings
) : Transformation() {
    
    override val cacheKey: String
        get() = "munsell_transform_${fileName}_${settings.hashCode()}"
    
    override suspend fun transform(input: Bitmap, size: coil3.size.Size): Bitmap {
        try {
            val imageBitmap = input.asComposeImageBitmap()
            val processedBitmap = MunsellDirectProcessor.processImage(imageBitmap, settings)
            return processedBitmap.asSkiaBitmap()
        } catch (e: Exception) {
            println("Error in MunsellBitmapTransform: ${e.message}")
            return input
        }
    }
}