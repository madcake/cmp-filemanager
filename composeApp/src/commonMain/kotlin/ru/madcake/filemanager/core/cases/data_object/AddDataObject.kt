package ru.madcake.filemanager.core.cases.data_object

import ru.madcake.filemanager.core.domain.DataObjectMeta
import ru.madcake.filemanager.designsystem.components.items.DataObjectInfo
import java.io.File

interface AddDataObject {
    suspend fun addDataObject(file: File, distance: DataObjectInfo? = null)

    suspend fun addDataObject(dataObject: DataObjectMeta, distance: DataObjectInfo? = null)
}