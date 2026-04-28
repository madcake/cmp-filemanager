package ru.madcake.filemanager.features.filesystempanel.di

import org.koin.dsl.module
import ru.madcake.filemanager.features.filesystempanel.viewmodels.FileSystemPanelViewModel

val fileSystemPanelModule = module {
    factory { FileSystemPanelViewModel(get(), get()) }
}