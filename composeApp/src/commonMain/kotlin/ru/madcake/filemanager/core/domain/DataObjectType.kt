package ru.madcake.filemanager.core.domain

import coil3.annotation.InternalCoilApi
import coil3.util.MimeTypeMap
import java.io.File

enum class DataObjectType {
    None,
    Image,
}

@OptIn(InternalCoilApi::class)
fun File.getObjectType(): DataObjectType {
    val mimeType = MimeTypeMap.getMimeTypeFromExtension(extension) // TODO: Custom realization

    val type = when (mimeType?.split("/")?.firstOrNull()) {
        "image" -> DataObjectType.Image
        else -> DataObjectType.None
    }
    return type
}