package ru.madcake.filemanager.features.main.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.madcake.filemanager.core.cases.data_object.AddDataObject
import ru.madcake.filemanager.designsystem.components.items.DataObjectInfo
import ru.madcake.filemanager.designsystem.components.items.FileObjectInfo


class MainViewModel(
    private val addDataObject: AddDataObject
) : ScreenModel {

    fun onCopy(obj: DataObjectInfo, distance: DataObjectInfo? = null) =
        screenModelScope.launch(Dispatchers.IO) {
            when (obj) {
                is FileObjectInfo -> addDataObject.addDataObject(
                    obj.file,
                    distance
                ) // TODO: We could change panels and opposite panel could be is file system
            }
        }

    fun onMove(obj: DataObjectInfo) {

    }

}