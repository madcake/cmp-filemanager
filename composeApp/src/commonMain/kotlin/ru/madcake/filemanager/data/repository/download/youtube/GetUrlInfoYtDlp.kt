package ru.madcake.filemanager.data.repository.download.youtube

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import ru.madcake.filemanager.core.cases.data_object.toHash
import ru.madcake.filemanager.downloadDir
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
class GetUrlInfoYtDlp {

    private val jsonEncoder = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    suspend fun getUrlInfo(url: String): VideoInfo {
        val downloadDir = downloadDir()
        val outDir = File(downloadDir, url.toHash())
        outDir.mkdir()
        val outInfoFile = File(outDir, "video.info.json")
        YtDlpDownloader().download(getYtDlpInfo(url, File(outDir, "video"))) {}
        val videoInfo = outInfoFile.inputStream().use {
            jsonEncoder.decodeFromStream<VideoInfo>(it)
        }
        return videoInfo
    }

}