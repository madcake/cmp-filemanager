package ru.madcake.filemanager.data.store.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.madcake.filemanager.data.store.room.entity.DbConfig

@Dao
interface DaoConfig {
    @Query("SELECT * FROM config WHERE name = :name")
    suspend fun get(name: String): DbConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun set(entry: DbConfig)
}