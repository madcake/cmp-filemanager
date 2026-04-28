package ru.madcake.filemanager.features.download.di

import org.koin.dsl.module
import ru.madcake.filemanager.features.download.viewmodels.YouTubeDownloadViewModel

val youTubeDownloadModule = module {
    factory { YouTubeDownloadViewModel(get()) }
}