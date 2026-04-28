package ru.madcake.filemanager.features.download.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import ru.madcake.filemanager.core.domain.download.youtube.DownloadProgress
import ru.madcake.filemanager.data.repository.download.youtube.DownloadYtDlp
import ru.madcake.filemanager.data.repository.download.youtube.GetUrlInfoYtDlp
import ru.madcake.filemanager.data.repository.download.youtube.VideoInfo

class YouTubeDownloadViewModel(
    private val getUrlInfoYtDlp: GetUrlInfoYtDlp,
) : ScreenModel, KoinComponent {

    val progress = MutableStateFlow<DownloadProgress?>(null)
    val videoInfo = MutableStateFlow<VideoInfo?>(null)
    val error = MutableStateFlow<String?>(null)

    fun search(request: String) = screenModelScope.launch {
        error.update { null }
        progress.update { null }
        videoInfo.update { null }
        val videoInfo = try {
            getUrlInfoYtDlp.getUrlInfo(request)
        } catch (err: Throwable) {
            error.update { err.message }
            return@launch
        }
        this@YouTubeDownloadViewModel.videoInfo.update { videoInfo }

    }

    fun download(formatId: String?) = screenModelScope.launch {
        val url = videoInfo.value?.webpageUrl ?: return@launch

        val formatId = formatId?.let {
            val format = videoInfo.value?.formats?.firstOrNull { it.formatId == formatId }
            if ((format?.acodec ?: "none") == "none") {
                "$formatId+bestaudio"
            } else {
                formatId
            }
        }

        DownloadYtDlp().download(url, formatId) { data ->
            progress.update { data }
        }
    }
}

