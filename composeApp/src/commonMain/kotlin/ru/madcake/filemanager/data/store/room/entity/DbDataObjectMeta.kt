package ru.madcake.filemanager.data.store.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.madcake.filemanager.core.domain.DataObjectMeta
import ru.madcake.filemanager.core.domain.DataObjectType

@Entity(
    tableName = "data_object_meta",
)
data class DbDataObjectMeta(
    @PrimaryKey val hash: String,
    val name: String,
    val contentType: DataObjectType,
    val size: Long,
    val createdAt: Long,
)

fun DbDataObjectMeta.toDomain(): DataObjectMeta {
    return DataObjectMeta(
        hash = hash,
        name = name,
        contentType = contentType,
        size = size,
        createdAt = createdAt,
    )
}

fun List<DbDataObjectMeta>.toDomain() = map { it.toDomain() }