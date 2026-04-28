package ru.madcake.filemanager.data.store.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "tree_object_link",
    primaryKeys = ["treeId", "objectId"],
    foreignKeys = [
        ForeignKey(DbBranchMeta::class, arrayOf("id"), arrayOf("treeId"), onDelete = ForeignKey.CASCADE),
        ForeignKey(DbDataObjectMeta::class, arrayOf("hash"), arrayOf("objectId"), onDelete = ForeignKey.CASCADE)
    ],
    indices = [
        Index(value = ["objectId"])
    ]
)
data class DbTreeObjectLink(
    val treeId: String,
    val objectId: String,
)
