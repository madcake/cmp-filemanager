package ru.madcake.filemanager.data.store.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.madcake.filemanager.core.domain.DataObjectNote
import ru.madcake.filemanager.data.store.room.entity.DbDataObjectNote

@Dao
interface DaoDataObjectNote {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(vararg dataObject: DbDataObjectNote)

    @Delete
    suspend fun delete(vararg dataObject: DbDataObjectNote)

    @Query("SELECT * FROM data_object_note")
    fun getAll(): Flow<List<DbDataObjectNote>>

    @Query("SELECT * FROM data_object_note WHERE id = :id")
    fun getNote(id: Long): Flow<DbDataObjectNote>

    @Query("SELECT id, objectId, data, createdAt, updatedAt FROM data_object_note WHERE objectId = :hash")
    fun getDataObjectNotes(hash: String): Flow<List<DbDataObjectNote>>
}