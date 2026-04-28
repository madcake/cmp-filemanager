package ru.madcake.filemanager.di

import org.koin.dsl.module
import ru.madcake.filemanager.core.cases.config.GetCurrentDir
import ru.madcake.filemanager.core.cases.config.SetCurrentDir
import ru.madcake.filemanager.core.cases.data_object.AddDataObject
import ru.madcake.filemanager.core.cases.data_object.GetDataObjects
import ru.madcake.filemanager.core.cases.data_object.RemoveBranchObject
import ru.madcake.filemanager.core.cases.data_object.RemoveDataObject
import ru.madcake.filemanager.core.cases.tree.CreateBranch
import ru.madcake.filemanager.core.cases.tree.GetBranch
import ru.madcake.filemanager.data.repository.config.ConfigRepository
import ru.madcake.filemanager.data.repository.data_object.DataObjectRepository
import ru.madcake.filemanager.data.repository.download.youtube.GetUrlInfoYtDlp
import ru.madcake.filemanager.data.repository.tree.TreeRepository

val repositoriesModule = module {
    single<ConfigRepository> { ConfigRepository(get()) }

    single<DataObjectRepository> { DataObjectRepository(get()) }

    single<TreeRepository> { TreeRepository(get(), get()) }

    single<GetUrlInfoYtDlp> { GetUrlInfoYtDlp() }

    single<GetCurrentDir> {
        val repository = get<ConfigRepository>()
        repository
    }

    single<SetCurrentDir> {
        val repository = get<ConfigRepository>()
        repository
    }

    single<AddDataObject> {
        val repository = get<DataObjectRepository>()
        repository
    }

    single<GetDataObjects> {
        val repository = get<DataObjectRepository>()
        repository
    }

    single<GetBranch> {
        val repository = get<TreeRepository>()
        repository
    }

    single<CreateBranch> {
        val repository = get<TreeRepository>()
        repository
    }

    single<RemoveDataObject> {
        val repository = get<TreeRepository>()
        repository
    }

    single<RemoveBranchObject> {
        val repository = get<TreeRepository>()
        repository
    }

}