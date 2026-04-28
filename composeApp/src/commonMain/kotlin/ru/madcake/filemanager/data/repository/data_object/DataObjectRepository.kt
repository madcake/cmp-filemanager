package ru.madcake.filemanager.data.repository.data_object

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.madcake.filemanager.core.cases.data_object.AddDataObject
import ru.madcake.filemanager.core.cases.data_object.FileHash
import ru.madcake.filemanager.core.cases.data_object.GetDataObjects
import ru.madcake.filemanager.core.domain.DataObjectMeta
import ru.madcake.filemanager.core.domain.getObjectType
import ru.madcake.filemanager.data.store.room.dao.DaoDataObject
import ru.madcake.filemanager.data.store.room.entity.DbDataObjectMeta
import ru.madcake.filemanager.data.store.room.entity.DbTreeObjectLink
import ru.madcake.filemanager.designsystem.components.items.DataObjectInfo
import ru.madcake.filemanager.designsystem.components.items.StoreBranchInfo
import ru.madcake.filemanager.workspaceDir
import java.io.File

class DataObjectRepository(
    private val daoDataObject: DaoDataObject,
) : GetDataObjects, AddDataObject {

    override fun getDataObjects(treeId: String?): Flow<DataObjectMeta> {
        return emptyFlow()
    }

    override suspend fun addDataObject(file: File, distance: DataObjectInfo?) {
        val hash = FileHash.getCheckSumSHA256FromFile(file)
        val targetFile = getTargetFile(hash)

        if (!targetFile.exists()) {
            file.copyTo(targetFile)
        }

        val meta = DbDataObjectMeta(
            hash = hash,
            name = file.name,
            contentType = file.getObjectType(),
            size = file.length(),
            createdAt = System.currentTimeMillis(),
        )
        val link = (distance as? StoreBranchInfo)?.let { DbTreeObjectLink(it.branchMeta.id, hash) }
        daoDataObject.create(meta, link)
    }

    override suspend fun addDataObject(dataObject: DataObjectMeta, distance: DataObjectInfo?) {

    }

    private fun getObjectDir(workspaceDir: File): File {
        val objectsDir = File(workspaceDir, "objects")
        if (!objectsDir.exists()) {
            objectsDir.mkdir()
        }
        return objectsDir
    }

    private fun getTargetFile(hash: String): File {
        val workspaceDir = workspaceDir()
        val objectsDir = getObjectDir(workspaceDir)
        return File(objectsDir, hash)
    }
}