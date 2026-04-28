package ru.madcake.filemanager.data.repository.download.youtube

import ru.madcake.filemanager.core.domain.download.youtube.DownloadProgress
import ru.madcake.filemanager.getDefaultDir
import java.io.File

class DownloadYtDlp {

    suspend fun download(url: String, formatId: String?, callback: (DownloadProgress) -> Unit) {
        val outFile = File(getDefaultDir(), "video.mkv")
        val request = getYtDlpDownload(url, outFile, formatId ?: "bestvideo+bestaudio")
        YtDlpDownloader().download(request, callback)
    }
}