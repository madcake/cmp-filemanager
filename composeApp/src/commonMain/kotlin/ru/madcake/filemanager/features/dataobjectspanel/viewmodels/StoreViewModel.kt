package ru.madcake.filemanager.features.dataobjectspanel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.madcake.filemanager.core.cases.data_object.RemoveBranchObject
import ru.madcake.filemanager.core.cases.data_object.RemoveDataObject
import ru.madcake.filemanager.core.cases.tree.CreateBranch
import ru.madcake.filemanager.core.cases.tree.GetBranch
import ru.madcake.filemanager.core.domain.BranchMeta
import ru.madcake.filemanager.core.domain.BranchType
import ru.madcake.filemanager.designsystem.components.items.DataObjectInfo
import ru.madcake.filemanager.designsystem.components.items.ParentObjectInfo
import ru.madcake.filemanager.designsystem.components.items.StoreBranchInfo
import ru.madcake.filemanager.designsystem.components.items.StoreObjectInfo

@OptIn(ExperimentalCoroutinesApi::class)
class StoreViewModel(
    private val getBranch: GetBranch,
    private val createBranch: CreateBranch,
    private val removeObject: RemoveDataObject,
    private val removeBranch: RemoveBranchObject,
) : ScreenModel {
    private val currentBranchMeta = MutableStateFlow<BranchMeta?>(null)

    val branch = currentBranchMeta.flatMapLatest { getBranch.getBranch(it?.id) }
        .map { branch ->
            if (branch.meta == null) {
                emptyList()
            } else {
                listOf(ParentObjectInfo(branch.parentMeta?.id ?: ""))
            } +
            branch.subBranchMetas.map { StoreBranchInfo(it) } +
                branch.dataObjects.map { StoreObjectInfo(it) }

        }
        .stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())

    fun createDir(name: String, type: BranchType = BranchType.Dir) = screenModelScope.launch(Dispatchers.IO) {
        createBranch.createBranch(currentBranchMeta.value?.id, name, type)
    }

    fun open(dataObjectInfo: DataObjectInfo, onDirOpen: (DataObjectInfo) -> Unit) = screenModelScope.launch(Dispatchers.IO) {
        when (dataObjectInfo) {
            is ParentObjectInfo -> {
                val id = dataObjectInfo.id.ifEmpty { null }
                val branch = getBranch.getBranch(id).firstOrNull()
                currentBranchMeta.update { branch?.meta }
            }
            is StoreObjectInfo -> {}
            is StoreBranchInfo -> {
                onDirOpen(dataObjectInfo)
                currentBranchMeta.update { dataObjectInfo.branchMeta }
            }
        }
    }

    fun remove(dataObjectInfo: DataObjectInfo) = screenModelScope.launch(Dispatchers.IO) {
        when (dataObjectInfo) {
            is StoreObjectInfo -> removeObject.removeDataObject(dataObjectInfo.dataObjectMeta, currentBranchMeta.value?.id)
            is StoreBranchInfo -> removeBranch.removeBranch(dataObjectInfo.branchMeta)
        }
    }
}