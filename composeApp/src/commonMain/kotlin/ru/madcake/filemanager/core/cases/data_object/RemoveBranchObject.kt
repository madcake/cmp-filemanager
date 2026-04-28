package ru.madcake.filemanager.core.cases.data_object

import ru.madcake.filemanager.core.domain.BranchMeta

interface RemoveBranchObject {
    suspend fun removeBranch(branch: BranchMeta)
}