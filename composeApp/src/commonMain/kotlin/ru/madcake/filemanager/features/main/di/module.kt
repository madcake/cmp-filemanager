package ru.madcake.filemanager.features.main.di

import org.koin.dsl.module
import ru.madcake.filemanager.features.main.viewmodels.MainViewModel

val mainScreenModule = module {
    factory { MainViewModel(get()) }
}