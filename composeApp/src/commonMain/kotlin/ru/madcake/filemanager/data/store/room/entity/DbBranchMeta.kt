package ru.madcake.filemanager.data.store.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.madcake.filemanager.core.domain.BranchMeta
import ru.madcake.filemanager.core.domain.BranchType

@Entity(tableName = "tree")
data class DbBranchMeta(
    @PrimaryKey val id: String,
    val name: String,
    val type: BranchType,
    val parent: String?,
    val createdAt: Long,
    val updatedAt: Long?,
)

fun DbBranchMeta.toDomain(): BranchMeta {
    return BranchMeta(
        id = id,
        name = name,
        type = type,
        parentId = parent,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
