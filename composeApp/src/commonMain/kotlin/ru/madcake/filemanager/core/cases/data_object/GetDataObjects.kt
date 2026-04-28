package ru.madcake.filemanager.core.cases.data_object

import kotlinx.coroutines.flow.Flow
import ru.madcake.filemanager.core.domain.DataObjectMeta

interface GetDataObjects {
    fun getDataObjects(treeId: String? = null): Flow<DataObjectMeta>
}