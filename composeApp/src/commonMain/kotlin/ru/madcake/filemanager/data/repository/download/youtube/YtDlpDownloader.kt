package ru.madcake.filemanager.data.repository.download.youtube

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import ru.madcake.filemanager.core.domain.download.youtube.DownloadProgress

class YtDlpDownloader {

    suspend fun download(request: YouTubeRequest, callback: (DownloadProgress) -> Unit) = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()

        val processBuilder = ProcessBuilder(request.command)

        val process = processBuilder.start()
        val outStream = process.inputStream
        val errStream = process.errorStream

        val stdOutProcessor = async { StreamProcessExtractor().extract(outStream, callback) }
        val stdErrProcessor = async { StreamGobbler().log(errStream) }

        val out = stdOutProcessor.await()
        val err = stdErrProcessor.await()
        val exitCode = process.waitFor()

        if (exitCode > 0) {
            throw Exception(err)
        }

        val elapsedTime = System.currentTimeMillis() - startTime
        println("$out - $err - $elapsedTime - $exitCode")
    }
}