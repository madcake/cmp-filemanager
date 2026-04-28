package ru.madcake.filemanager.core.cases.tree

import kotlinx.coroutines.flow.Flow
import ru.madcake.filemanager.core.domain.Branch

interface GetBranch {
    fun getBranch(branchId: String? = null): Flow<Branch>
}