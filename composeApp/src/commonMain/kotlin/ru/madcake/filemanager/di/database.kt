package ru.madcake.filemanager.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.madcake.filemanager.data.store.room.ObjectsDatabase
import ru.madcake.filemanager.data.store.room.dao.DaoConfig
import ru.madcake.filemanager.data.store.room.dao.DaoDataObject
import ru.madcake.filemanager.data.store.room.dao.DaoDataObjectNote
import ru.madcake.filemanager.data.store.room.dao.DaoTreeObject

internal expect fun sqlDriverModule(): Module

val databaseModule = module {
    includes(
        sqlDriverModule(),
    )

    single<ObjectsDatabase> {
        get<RoomDatabase.Builder<ObjectsDatabase>>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    single<DaoConfig> {
        val database = get<ObjectsDatabase>()
        database.getDaoConfig()
    }

    single<DaoTreeObject> {
        val database = get<ObjectsDatabase>()
        database.getDaoTree()
    }

    single<DaoDataObject> {
        val database = get<ObjectsDatabase>()
        database.getDaoDataObject()
    }

    single<DaoDataObjectNote> {
        val database = get<ObjectsDatabase>()
        database.getDaoDataObjectNote()
    }

}