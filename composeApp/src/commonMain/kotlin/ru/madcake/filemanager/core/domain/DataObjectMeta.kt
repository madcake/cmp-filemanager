package ru.madcake.filemanager.core.domain

data class DataObjectMeta(
    val hash: String,
    val name: String,
    val contentType: DataObjectType,
    val size: Long = 0,
    val createdAt: Long,
)

