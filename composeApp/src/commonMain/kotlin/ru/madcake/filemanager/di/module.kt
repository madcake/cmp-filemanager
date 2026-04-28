package ru.madcake.filemanager.di

import org.koin.dsl.module
import ru.madcake.filemanager.features.dataobjectspanel.di.objectsPanelModule
import ru.madcake.filemanager.features.download.di.youTubeDownloadModule
import ru.madcake.filemanager.features.filesystempanel.di.fileSystemPanelModule
import ru.madcake.filemanager.features.main.di.mainScreenModule

val mainModule = module {
    includes(databaseModule)

    includes(mainScreenModule)
    includes(fileSystemPanelModule)
    includes(objectsPanelModule)
    includes(youTubeDownloadModule)

    includes(repositoriesModule)
}