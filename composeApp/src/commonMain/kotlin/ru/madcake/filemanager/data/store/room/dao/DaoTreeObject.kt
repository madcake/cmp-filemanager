package ru.madcake.filemanager.data.store.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.madcake.filemanager.data.store.room.entity.DbBranchMeta

@Dao
interface DaoTreeObject {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun create(vararg tree: DbBranchMeta)

    @Delete
    suspend fun delete(vararg tree: DbBranchMeta)

    @Update
    suspend fun update(vararg tree: DbBranchMeta)

    @Query("SELECT * FROM tree WHERE id = :id")
    fun getBranchMeta(id: String?): Flow<DbBranchMeta?>

    @Query("""
        SELECT * FROM tree WHERE
            id = (SELECT parent FROM tree WHERE id = :id)
    """)
    fun getParentBranchMeta(id: String?): Flow<DbBranchMeta?>

    @Query("""
        SELECT * FROM tree WHERE parent = :id OR (parent IS NULL AND true = :isRoot)
    """)
    fun getSubBranches(id: String?, isRoot: Boolean = false): Flow<List<DbBranchMeta>>
}