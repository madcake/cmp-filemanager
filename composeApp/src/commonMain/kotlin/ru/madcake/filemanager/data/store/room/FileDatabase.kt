package ru.madcake.filemanager.data.store.room

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.madcake.filemanager.data.store.room.dao.DaoConfig
import ru.madcake.filemanager.data.store.room.dao.DaoDataObject
import ru.madcake.filemanager.data.store.room.dao.DaoDataObjectNote
import ru.madcake.filemanager.data.store.room.dao.DaoTreeObject
import ru.madcake.filemanager.data.store.room.entity.DbBranchMeta
import ru.madcake.filemanager.data.store.room.entity.DbConfig
import ru.madcake.filemanager.data.store.room.entity.DbDataObjectMeta
import ru.madcake.filemanager.data.store.room.entity.DbDataObjectNote
import ru.madcake.filemanager.data.store.room.entity.DbTreeObjectLink

@Database(
    entities = [
        DbConfig::class,
        DbBranchMeta::class,
        DbDataObjectMeta::class,
        DbTreeObjectLink::class,
        DbDataObjectNote::class,
    ],
    version = 2,
    exportSchema = true,
)
abstract class ObjectsDatabase : RoomDatabase() {
    abstract fun getDaoConfig(): DaoConfig

    abstract fun getDaoTree(): DaoTreeObject

    abstract fun getDaoDataObject(): DaoDataObject

    abstract fun getDaoDataObjectNote(): DaoDataObjectNote
}