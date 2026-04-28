package ru.madcake.filemanager.core.domain

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path

class ImageUtilsTest {

    @Test
    fun testJpegMagicNumber() {
        val testFile = createTempFileWithMagicNumber(byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte()))
        assertTrue(ImageUtils.isImageFile(testFile), "Should detect JPEG by magic number")
        testFile.delete()
    }

    @Test
    fun testPngMagicNumber() {
        val testFile = createTempFileWithMagicNumber(byteArrayOf(
            0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte(),
            0x0D.toByte(), 0x0A.toByte(), 0x1A.toByte(), 0x0A.toByte()
        ))
        assertTrue(ImageUtils.isImageFile(testFile), "Should detect PNG by magic number")
        testFile.delete()
    }

    @Test
    fun testGifMagicNumber() {
        val testFile = createTempFileWithMagicNumber(byteArrayOf(
            0x47.toByte(), 0x49.toByte(), 0x46.toByte(), 0x38.toByte(), 0x39.toByte(), 0x61.toByte()
        ))
        assertTrue(ImageUtils.isImageFile(testFile), "Should detect GIF by magic number")
        testFile.delete()
    }

    @Test
    fun testBmpMagicNumber() {
        val testFile = createTempFileWithMagicNumber(byteArrayOf(0x42.toByte(), 0x4D.toByte()))
        assertTrue(ImageUtils.isImageFile(testFile), "Should detect BMP by magic number")
        testFile.delete()
    }

    @Test
    fun testWebPMagicNumber() {
        val testFile = createTempFileWithMagicNumber(byteArrayOf(
            0x52.toByte(), 0x49.toByte(), 0x46.toByte(), 0x46.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x57.toByte(), 0x45.toByte(), 0x42.toByte(), 0x50.toByte()
        ))
        assertTrue(ImageUtils.isImageFile(testFile), "Should detect WebP by magic number")
        testFile.delete()
    }

    @Test
    fun testTiffLittleEndianMagicNumber() {
        val testFile = createTempFileWithMagicNumber(byteArrayOf(
            0x49.toByte(), 0x49.toByte(), 0x2A.toByte(), 0x00.toByte()
        ))
        assertTrue(ImageUtils.isImageFile(testFile), "Should detect TIFF (little endian) by magic number")
        testFile.delete()
    }

    @Test
    fun testTiffBigEndianMagicNumber() {
        val testFile = createTempFileWithMagicNumber(byteArrayOf(
            0x4D.toByte(), 0x4D.toByte(), 0x00.toByte(), 0x2A.toByte()
        ))
        assertTrue(ImageUtils.isImageFile(testFile), "Should detect TIFF (big endian) by magic number")
        testFile.delete()
    }

    @Test
    fun testIcoMagicNumber() {
        val testFile = createTempFileWithMagicNumber(byteArrayOf(
            0x00.toByte(), 0x00.toByte(), 0x01.toByte(), 0x00.toByte()
        ))
        assertTrue(ImageUtils.isImageFile(testFile), "Should detect ICO by magic number")
        testFile.delete()
    }

    @Test
    fun testNonImageFile() {
        val testFile = createTempFileWithMagicNumber(byteArrayOf(
            0x25.toByte(), 0x50.toByte(), 0x44.toByte(), 0x46.toByte() // PDF magic number
        ))
        assertFalse(ImageUtils.isImageFile(testFile), "Should not detect PDF as image")
        testFile.delete()
    }

    @Test
    fun testEmptyFile() {
        val testFile = createTempFileWithMagicNumber(byteArrayOf())
        assertFalse(ImageUtils.isImageFile(testFile), "Should not detect empty file as image")
        testFile.delete()
    }

    @Test
    fun testNonExistentFile() {
        val nonExistentFile = File("non_existent_file.txt")
        assertFalse(ImageUtils.isImageFile(nonExistentFile), "Should not detect non-existent file as image")
    }

    @Test
    fun testPathBasedDetection() {
        val testFile = createTempFileWithMagicNumber(byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte()))
        assertTrue(ImageUtils.isImageFile(testFile.absolutePath), "Should detect JPEG by path")
        testFile.delete()
    }

    @Test
    fun testInsufficientDataFile() {
        val testFile = createTempFileWithMagicNumber(byteArrayOf(0xFF.toByte()))
        assertFalse(ImageUtils.isImageFile(testFile), "Should not detect file with insufficient data as image")
        testFile.delete()
    }

    private fun createTempFileWithMagicNumber(magicBytes: ByteArray): File {
        val tempFile = Files.createTempFile("test_image", ".tmp").toFile()
        if (magicBytes.isNotEmpty()) {
            FileOutputStream(tempFile).use { fos ->
                fos.write(magicBytes)
                // Add some additional bytes to make it look more like a real file
                fos.write(ByteArray(100) { 0x00 })
            }
        }
        return tempFile
    }
}