package ru.madcake.filemanager.data.store.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.madcake.filemanager.data.store.room.entity.DbBranchMeta
import ru.madcake.filemanager.data.store.room.entity.DbDataObjectMeta
import ru.madcake.filemanager.data.store.room.entity.DbTreeObjectLink

@Dao
interface DaoDataObject {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun create(dataObject: DbDataObjectMeta)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun link(dataObject: DbTreeObjectLink)

    @Transaction
    suspend fun create(dataObject: DbDataObjectMeta, distance: DbTreeObjectLink? = null) {
        create(dataObject)
        distance?.let { link(distance) }
    }

    @Delete
    suspend fun delete(vararg dataObject: DbDataObjectMeta)

    @Query(
        """
            DELETE FROM tree_object_link
            WHERE treeId = :treeId AND objectId = :hash
        """
    )
    suspend fun deleteLink(hash: String, treeId: String?)

    @Query("SELECT * FROM data_object_meta")
    fun getAll(): Flow<List<DbDataObjectMeta>>

    @Query("SELECT * FROM data_object_meta WHERE hash = :hash")
    fun getMeta(hash: String): Flow<DbDataObjectMeta>

    @Query("SELECT * FROM data_object_meta WHERE hash = :hash")
    fun getValue(hash: String): Flow<DbDataObjectMeta>

    @Query("""
        SELECT data_object_meta.* FROM data_object_meta
        LEFT JOIN tree_object_link ON data_object_meta.hash = tree_object_link.objectId 
        WHERE treeId = :treeId OR (treeId IS NULL AND true = :isRoot)
        """
    )
    fun getMetaByBranch(treeId: String?, isRoot: Boolean = false): Flow<List<DbDataObjectMeta>>

    @Query("""
        SELECT data_object_meta.* FROM data_object_meta
        LEFT JOIN tree_object_link ON data_object_meta.hash = tree_object_link.objectId 
        WHERE treeId IN (:treeIds)
        """
    )
    fun getMetaByBranches(treeIds: List<String>): Flow<List<DbDataObjectMeta>>

    @Query("""
        SELECT tree.* FROM tree
        JOIN tree_object_link ON tree.id = tree_object_link.treeId 
        WHERE objectId = :hash
        """
    )
    fun getParents(hash: String): Flow<List<DbBranchMeta>>
}