package ru.madcake.filemanager.data.repository.download.youtube

import java.io.File

class YouTubeRequest(val command: List<String>) {
    class YtDlpBuilder(private val url: String) {
        private val args: MutableList<String> = mutableListOf()
        private var outputFile: File? = null

        fun addArg(key: String, value: String? = null): YtDlpBuilder {
            args.add(key)
            value?.let { args.add(it) }
            return this
        }

        fun setOutputFile(file: File): YtDlpBuilder {
            outputFile = file
            return this
        }

        fun build(): YouTubeRequest {
            return YouTubeRequest(
                args.apply {
                    // TODO: Calculate path to yt-dlp and check on available
                    add(0, "/opt/homebrew/Cellar/yt-dlp/2025.5.22/bin/yt-dlp")
                    outputFile?.absolutePath?.let {
                        val outputIndex = args.indexOf("-o")
                        if (outputIndex == -1) {
                            add("-o")
                            add(it)
                        } else {
                            set(outputIndex + 1, it)
                        }
                    }
                    add(url)
                }
            )
        }
    }
}

fun getYtDlpInfo(url: String, outputFile: File): YouTubeRequest {
    return YouTubeRequest.YtDlpBuilder(url)
        .addArg("--ignore-errors")
        .addArg("--write-info-json")
        .addArg("--skip-download")
        .addArg("--cookies", "/Users/madcake/cookies.txt")
        .setOutputFile(outputFile)
        .build()
}

fun getYtDlpDownload(url: String, outputFile: File, quality: String = "bestvideo+bestaudio"): YouTubeRequest {
    return YouTubeRequest.YtDlpBuilder(url)
        .addArg("--ignore-errors")
        .addArg("--write-annotations")
        .addArg("--write-description")
        .addArg("--write-sub")
        .addArg("--cookies", "/Users/madcake/cookies.txt")
        .addArg("-f", quality)
        .setOutputFile(outputFile)
        .build()
}