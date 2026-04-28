package ru.madcake.filemanager.data.store.room.view

import androidx.room.DatabaseView
import ru.madcake.filemanager.core.domain.BranchType

// TODO: Not implemented
@DatabaseView(
    "SELECT * FROM tree, data_object"
)
data class TreeView(
    val treeId: String,
    val treeName: String,
    val branchType: BranchType,
    val treeUpdatedAt: String,
    val treeCreatedAt: String,
    val objectHash: String,
    val objectName: String,
    val objectSize: String,
    val objectCreatedAt: String,
    val objectNotesCount: String,
)