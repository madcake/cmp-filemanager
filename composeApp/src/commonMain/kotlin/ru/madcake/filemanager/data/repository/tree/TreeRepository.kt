package ru.madcake.filemanager.data.repository.tree

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import ru.madcake.filemanager.core.cases.data_object.RemoveBranchObject
import ru.madcake.filemanager.core.cases.data_object.RemoveDataObject
import ru.madcake.filemanager.core.cases.data_object.toHash
import ru.madcake.filemanager.core.cases.tree.CreateBranch
import ru.madcake.filemanager.core.cases.tree.GetBranch
import ru.madcake.filemanager.core.domain.Branch
import ru.madcake.filemanager.core.domain.BranchMeta
import ru.madcake.filemanager.core.domain.BranchType
import ru.madcake.filemanager.core.domain.DataObjectMeta
import ru.madcake.filemanager.data.store.room.dao.DaoDataObject
import ru.madcake.filemanager.data.store.room.dao.DaoTreeObject
import ru.madcake.filemanager.data.store.room.entity.DbBranchMeta
import ru.madcake.filemanager.data.store.room.entity.toDomain
import ru.madcake.filemanager.workspaceDir
import java.io.File

class TreeRepository(
    private val daoTreeObject: DaoTreeObject,
    private val daoDataObject: DaoDataObject,
) : GetBranch,
    CreateBranch,
    RemoveDataObject,
    RemoveBranchObject
{

    override fun getBranch(branchId: String?): Flow<Branch> = combine(
        daoTreeObject.getBranchMeta(branchId),
        daoTreeObject.getParentBranchMeta(branchId),
        daoDataObject.getMetaByBranch(branchId, branchId == null).map { items -> items.map { it.toDomain() } },
        daoTreeObject.getSubBranches(branchId, branchId == null).map { items -> items.map { it.toDomain() } },
    ) { meta, parentMeta, dataObjects, treeObjects ->
        Branch(meta?.toDomain(), parentMeta?.toDomain(), treeObjects, dataObjects)
    }

    override suspend fun createBranch(parentBranchId: String?, name: String, type: BranchType) {
        val parentBranch = getBranch(parentBranchId).firstOrNull()
        val path = mutableListOf(parentBranch?.meta?.name)

        var tempBranch = parentBranch?.parentMeta
        while (tempBranch != null) {
            path.add(tempBranch.name)
            tempBranch = getBranch(tempBranch.parentId).firstOrNull()?.meta
        }
        val pathString = path.apply { add(0, name) }.filterNotNull().joinToString("/")
        val meta = DbBranchMeta(
            id = pathString.toHash(),
            name = name,
            type = type,
            parent = parentBranchId,
            createdAt = System.currentTimeMillis(),
            updatedAt = 0,
        )
        daoTreeObject.create(meta)
    }

    override suspend fun removeDataObject(obj: DataObjectMeta, branchId: String?) {
        val dbObj = daoDataObject.getMeta(obj.hash).firstOrNull() ?: return
        val linkCounts = daoDataObject.getParents(obj.hash)
            .map { it.size }
            .firstOrNull() ?: 0
        // TODO: Show errors
        if (linkCounts > 1) {
            daoDataObject.deleteLink(dbObj.hash, branchId)
        } else {
            daoDataObject.delete(dbObj)
            File(File(workspaceDir(), "objects"), dbObj.hash)
        }
    }

    override suspend fun removeBranch(branch: BranchMeta) {
        val dbBranchMeta = daoTreeObject.getBranchMeta(branch.id).firstOrNull() ?: return // todo: Show errors
        val branches = mutableListOf<DbBranchMeta>()
        val queue = ArrayDeque<DbBranchMeta>()
        queue.add(dbBranchMeta)

        while(queue.isNotEmpty()) {
            val item = queue.removeFirst()
            branches.add(item)
            val subBranches = daoTreeObject.getSubBranches(item.id).firstOrNull() ?: emptyList()
            queue.addAll(subBranches)
        }
        branches.map {
            val objs = daoDataObject.getMetaByBranch(it.id, false)
                .firstOrNull()
                ?.toDomain() ?: emptyList()
            Pair(it, objs)
        }
        .forEach { pair -> pair.second.forEach { removeDataObject(it, pair.first.id) } }
        daoTreeObject.delete(*branches.toTypedArray())
    }
}