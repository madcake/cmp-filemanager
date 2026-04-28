package ru.madcake.filemanager.core.domain

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files

sealed class ImageFormat(
    val name: String,
    val magicBytes: ByteArray,
    val requiredLength: Int = magicBytes.size
) {
    object JPEG : ImageFormat(
        name = "JPEG",
        magicBytes = byteArrayOf(0xFF.toByte(), 0xD8.toByte())
    )
    
    object PNG : ImageFormat(
        name = "PNG", 
        magicBytes = byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte())
    )
    
    object GIF : ImageFormat(
        name = "GIF",
        magicBytes = byteArrayOf(0x47.toByte(), 0x49.toByte(), 0x46.toByte())
    )
    
    object BMP : ImageFormat(
        name = "BMP",
        magicBytes = byteArrayOf(0x42.toByte(), 0x4D.toByte())
    )
    
    object WebP : ImageFormat(
        name = "WebP",
        magicBytes = byteArrayOf(
            0x52.toByte(), 0x49.toByte(), 0x46.toByte(), 0x46.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x57.toByte(), 0x45.toByte(), 0x42.toByte(), 0x50.toByte()
        ),
        requiredLength = 12
    )
    
    object TIFF_LE : ImageFormat(
        name = "TIFF (Little Endian)",
        magicBytes = byteArrayOf(0x49.toByte(), 0x49.toByte(), 0x2A.toByte(), 0x00.toByte())
    )
    
    object TIFF_BE : ImageFormat(
        name = "TIFF (Big Endian)",
        magicBytes = byteArrayOf(0x4D.toByte(), 0x4D.toByte(), 0x00.toByte(), 0x2A.toByte())
    )
    
    object ICO : ImageFormat(
        name = "ICO",
        magicBytes = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x01.toByte(), 0x00.toByte())
    )
    
    companion object {
        val ALL_FORMATS = listOf(JPEG, PNG, GIF, BMP, WebP, TIFF_LE, TIFF_BE, ICO)
    }
}

object ImageUtils {
    
    fun isImageFile(file: File): Boolean {
        return try {
            if (!file.exists() || !file.isFile) {
                false
            } else {
                val mimeType = Files.probeContentType(file.toPath())
                if (mimeType != null && mimeType.startsWith("image/")) {
                    true
                } else {
                    isImageByContent(file)
                }
            }
        } catch (e: IOException) {
            // Fallback to content-based detection if MIME type detection fails
            isImageByContent(file)
        }
    }
    
    fun isImageFile(path: String): Boolean {
        return isImageFile(File(path))
    }
    
    private fun isImageByContent(file: File): Boolean {
        return try {
            FileInputStream(file).use { fis ->
                val header = ByteArray(12)
                val bytesRead = fis.read(header)
                if (bytesRead < 4) return false
                
                // Check against all known image formats
                ImageFormat.ALL_FORMATS.any { format ->
                    bytesRead >= format.requiredLength && matchesMagicBytes(header, format)
                }
            }
        } catch (e: Exception) {
            false
        }
    }
    
    private fun matchesMagicBytes(header: ByteArray, format: ImageFormat): Boolean {
        return when (format) {
            is ImageFormat.WebP -> {
                // WebP has a special pattern: RIFF + 4 bytes + WEBP
                format.magicBytes.indices.all { i ->
                    when (i) {
                        4, 5, 6, 7 -> true // Skip file size bytes
                        else -> header[i] == format.magicBytes[i]
                    }
                }
            }
            else -> {
                // Standard magic byte comparison
                format.magicBytes.indices.all { i ->
                    header[i] == format.magicBytes[i]
                }
            }
        }
    }
}