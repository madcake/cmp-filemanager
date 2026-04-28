package ru.madcake.filemanager.features.download.presents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import coil3.compose.AsyncImage
import ru.madcake.filemanager.data.repository.download.youtube.VideoFormat
import ru.madcake.filemanager.designsystem.theme.Icon
import ru.madcake.filemanager.designsystem.theme.Spacer8
import ru.madcake.filemanager.designsystem.theme.padding16
import ru.madcake.filemanager.features.download.viewmodels.YouTubeDownloadViewModel


class YouTubeDownloadDialog : Screen {

    @Composable
    override fun Content() {
        val viewModel = getScreenModel<YouTubeDownloadViewModel>()
        val videoInfo by viewModel.videoInfo.collectAsStateWithLifecycle()
        val error by viewModel.error.collectAsStateWithLifecycle()
        val progress by viewModel.progress.collectAsStateWithLifecycle()

        LazyColumn {
            youTubeSearch(viewModel::search)
            error(error)
            thumbnails(videoInfo?.thumbnail)
            title(videoInfo?.title)
            if (progress != null) {
                item {
                    LinearProgressIndicator(progress!!.progress)
                    Text("${progress?.eta} : ${progress?.rate} : ${progress?.size}")
                }
            } else {
                videoInfo?.let { formats(it.formats, viewModel::download) }

            }
            description(videoInfo?.description)
        }
    }

    private fun LazyListScope.error(message: String?) {
        message?.takeIf { it.isNotEmpty() } ?: return
        item {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }

    private fun LazyListScope.thumbnails(url: String?) {
        url?.takeIf { it.isNotEmpty() } ?: return
        item {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .clip(RoundedCornerShape(topStart = padding16, topEnd = padding16)),
                model = url,
                contentScale = ContentScale.Crop,
                contentDescription = "",
            )
            Spacer8()
        }
    }

    private fun LazyListScope.formats(formats: List<VideoFormat>?, onDownload: (String?) -> Unit) {
        formats?.takeIf { it.isNotEmpty() }
            ?.filter { it.fps > 0 }
            ?.groupBy { "${it.width}x${it.height}" }
            ?.map { resolution -> resolution.value.minBy { it.filesize } }
            ?.forEach {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton({ onDownload(it.formatId) }) {
                            Icon(Icons.Default.Download)
                        }
                        Text("${it.resolution} (${it.fps.toInt()} fps) - ${it.formattedFileSize}")
                    }
                }
            }
    }

    private fun LazyListScope.title(title: String?) {
        title?.takeIf { it.isNotEmpty() } ?: return
        item {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer8()
        }
    }

    private fun LazyListScope.description(text: String?) {
        text?.takeIf { it.isNotEmpty() } ?: return
        item {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    private fun LazyListScope.youTubeSearch(
        onSearch: (String) -> Unit,
    ) {
        item {
            var request by remember { mutableStateOf("") }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    BasicTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = request,
                        maxLines = 1,
                        onValueChange = { request = it },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions( onDone =  { onSearch(request) }),
                    )
                    if (request.isEmpty()) {
                        Text(
                            text = "YouTube URL (https://www.youtube.com/watch?v=xxxx)",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                IconButton(onClick = { onSearch(request) }) {
                    Icon(Icons.Default.Search, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}