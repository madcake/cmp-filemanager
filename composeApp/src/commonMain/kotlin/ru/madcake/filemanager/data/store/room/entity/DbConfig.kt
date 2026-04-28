package ru.madcake.filemanager.data.store.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.madcake.filemanager.core.domain.ConfigValueType

@Entity(tableName = "config")
data class DbConfig(
    @PrimaryKey val name: String,
    val value: String,
    val type: ConfigValueType,
)
