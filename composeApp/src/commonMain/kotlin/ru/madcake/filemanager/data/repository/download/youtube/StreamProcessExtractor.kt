package ru.madcake.filemanager.data.repository.download.youtube

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.madcake.filemanager.core.domain.download.youtube.DownloadProgress
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.util.regex.Pattern

class StreamProcessExtractor {
    private val p: Pattern = Pattern.compile("\\[download\\]\\s+(\\d+\\.\\d)% of (.*) at (.*) ETA (\\d+):(\\d+)")

    suspend fun extract(stream: InputStream, callback: (DownloadProgress) -> Unit): String {
        val buffer = StringBuffer()
        val reader: Reader = withContext(Dispatchers.IO) { InputStreamReader(stream, "UTF-8") }
        val currentLine = StringBuilder()
        var nextChar: Int
        while ((withContext(Dispatchers.IO) { reader.read() }.also { nextChar = it }) != -1) {
            buffer.append(nextChar.toChar())
            if (nextChar == '\r'.code) {
                processOutputLine(currentLine.toString())?.let { callback(it) }
                currentLine.setLength(0)
                continue
            }
            currentLine.append(nextChar.toChar())
        }
        return buffer.toString()
    }

    private fun processOutputLine(line: String): DownloadProgress? {
        val match = p.matcher(line)
        return if (match.matches()) {
            DownloadProgress(
                progress = match.group(GROUP_PERCENT).toFloat() / 100,
                eta = convertToSeconds(match.group(GROUP_MINUTES), match.group(GROUP_SECONDS)).toLong(),
                size = convertToBits(match.group(GROUP_SIZE)),
                rate = convertToBits(match.group(GROUP_RATE))
            )
        } else {
            null
        }

    }

    private fun convertToSeconds(minutes: String, seconds: String): Int {
        return minutes.toInt() * 60 + seconds.toInt()
    }

    private fun convertToBits(size: String): Long {
        return if (!Character.isDigit(size[0])) {
            size.substring(1)
        } else {
            size
        }.let {
            if (size.contains("KiB")) {
                if (size[size.length - 1] == 's') {
                    val inp = size.substring(0, size.length - 5).toFloat()
                    KIB_FACTOR * Math.round(inp)
                } else {
                    val inp = size.substring(0, size.length - 4).toFloat()
                    KIB_FACTOR * Math.round(inp)
                }
            } else if (size.contains("MiB")) {
                if (size[size.length - 1] == 's') {
                    val inp = size.substring(0, size.length - 5).toFloat()
                    MIB_FACTOR * Math.round(inp)
                } else {
                    val inp = size.substring(0, size.length - 4).toFloat()
                    MIB_FACTOR * Math.round(inp)
                }
            } else {
                0L
            }
        }
    }

    companion object {
        private const val GROUP_PERCENT = 1
        private const val GROUP_SIZE = 2
        private const val GROUP_RATE = 3
        private const val GROUP_MINUTES = 4
        private const val GROUP_SECONDS = 5
        private const val KIB_FACTOR: Long = 1024
        private const val MIB_FACTOR = 1024 * KIB_FACTOR
    }
}