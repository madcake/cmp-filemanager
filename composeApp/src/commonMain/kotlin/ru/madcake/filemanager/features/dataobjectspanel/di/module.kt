package ru.madcake.filemanager.features.dataobjectspanel.di

import org.koin.dsl.module
import ru.madcake.filemanager.features.dataobjectspanel.viewmodels.StoreViewModel

val objectsPanelModule = module {
    factory { StoreViewModel(get(), get(), get(), get()) }
}