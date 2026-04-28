package ru.madcake.filemanager.data.repository.download.youtube

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.NumberFormat

@Serializable
data class VideoInfo(
    val id: String? = null,
    val fulltitle: String? = null,
    val title: String? = null,
    @SerialName("upload_date") val uploadDate: String? = null,
    val duration: Int = 0,
    val description: String? = null,
    val thumbnail: String? = null,
    val license: String? = null,
    @SerialName("webpage_url") val webpageUrl: String? = null,
    @SerialName("webpage_url_basename") val webpageUrlBasename: String? = null,
    val resolution: String? = null,
    val width: Int = 0,
    val height: Int = 0,
    val formats: ArrayList<VideoFormat>? = null,
)

@Serializable
class VideoFormat(
    @SerialName("format_id") val formatId: String? = null,
    val width: Int = 0,
    val height: Int = 0,
    val filesize: Long = 0,
    val fps: Double = 0.0,
    val url: String? = null,
    val acodec: String? = null,
) {
    val formattedFileSize: String
        get() {
            var size = filesize
            var index = 0
            do {
                size /= 1024
                index++
            } while (size > 1000)
            return "${NumberFormat.getInstance().apply { maximumFractionDigits = 2 }.format(size)}${filesizeMeasures[index]}"
        }

    val resolution: String
        get() = "${height}p"

    companion object {
        val filesizeMeasures = listOf(
            "B",
            "Kb",
            "Mb",
            "Tb",
            "Pb",
            "Eb",
            "Zb",
        )
    }
}