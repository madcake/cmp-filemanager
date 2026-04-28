package ru.madcake.filemanager.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.madcake.filemanager.data.store.room.ObjectsDatabase
import ru.madcake.filemanager.workspaceDir
import java.io.File

actual fun sqlDriverModule(): Module {
    return module {
        single<RoomDatabase.Builder<ObjectsDatabase>> { getDatabaseBuilder() }
    }
}

fun getDatabaseBuilder(): RoomDatabase.Builder<ObjectsDatabase> {
    val dbFile = File(workspaceDir(), "database.db")
    return Room.databaseBuilder<ObjectsDatabase>(name = dbFile.absolutePath)
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(dropAllTables = true)
}
