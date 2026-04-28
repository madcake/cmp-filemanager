package ru.madcake.filemanager.core.domain

data class BranchMeta(
    val id: String,
    val name:String,
    val type: BranchType,
    val parentId: String?,
    val createdAt: Long,
    val updatedAt: Long?,
)
