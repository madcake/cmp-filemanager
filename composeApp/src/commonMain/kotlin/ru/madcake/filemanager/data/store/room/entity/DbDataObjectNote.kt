package ru.madcake.filemanager.data.store.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "data_object_note",
    foreignKeys = [
        ForeignKey(DbDataObjectMeta::class, ["hash"], ["objectId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [
        Index(value = ["objectId"])
    ]
)
data class DbDataObjectNote(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val objectId: String,
    val data: String,
    val createdAt: Long,
    val updatedAt: Long,
)
