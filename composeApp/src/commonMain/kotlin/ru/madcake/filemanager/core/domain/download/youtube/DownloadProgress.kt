package ru.madcake.filemanager.core.domain.download.youtube

data class DownloadProgress(
    val progress: Float,
    val eta: Long,
    val rate: Long,
    val size: Long,
)