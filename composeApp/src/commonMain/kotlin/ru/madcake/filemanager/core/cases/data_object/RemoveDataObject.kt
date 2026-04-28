package ru.madcake.filemanager.core.cases.data_object

import ru.madcake.filemanager.core.domain.DataObjectMeta

interface RemoveDataObject {
    suspend fun removeDataObject(obj: DataObjectMeta, branchId: String?)
}