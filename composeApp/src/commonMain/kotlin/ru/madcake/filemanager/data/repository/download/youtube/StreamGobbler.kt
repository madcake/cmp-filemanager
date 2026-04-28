package ru.madcake.filemanager.data.repository.download.youtube

import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

class StreamGobbler {

    fun log(stream: InputStream): String {
        val buffer = StringBuffer()
        val reader: Reader = InputStreamReader(stream, "UTF-8")
        var nextChar: Int
        while ((reader.read().also { nextChar = it }) != -1) {
            buffer.append(nextChar.toChar())
        }
        return buffer.toString()
    }
}