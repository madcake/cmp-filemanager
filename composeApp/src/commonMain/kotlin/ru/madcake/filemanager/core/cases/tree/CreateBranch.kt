package ru.madcake.filemanager.core.cases.tree

import ru.madcake.filemanager.core.domain.BranchType

interface CreateBranch {
    suspend fun createBranch(parentBranchId: String?, name: String, type: BranchType)
}