package ru.madcake.filemanager.features.filesystempanel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import ru.madcake.filemanager.core.cases.config.GetCurrentDir
import ru.madcake.filemanager.core.cases.config.SetCurrentDir
import ru.madcake.filemanager.core.cases.data_object.listDirectoryEntriesFlow
import ru.madcake.filemanager.core.cases.flow.mutableStateIn
import ru.madcake.filemanager.designsystem.components.items.DataObjectInfo
import ru.madcake.filemanager.designsystem.components.items.FileObjectInfo
import ru.madcake.filemanager.designsystem.components.items.ParentObjectInfo
import java.awt.Desktop
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class FileSystemPanelViewModel(
    getCurrentDir: GetCurrentDir,
    private val setCurrentDir: SetCurrentDir,
) : ScreenModel, KoinComponent {

    private val currentDir = flow { emit(getCurrentDir.getCurrentDir()) }
        .mutableStateIn(screenModelScope, null)

    private val showHidden = MutableStateFlow(false)

    val container = currentDir.filterNotNull()
        .mapLatest { ParentObjectInfo(it.absolutePath) } // TODO: FileObject and etc...
        .stateIn(screenModelScope, SharingStarted.Eagerly, null)

    val filesInCurrentDir = currentDir.filterNotNull().filter { it.isDirectory }.flatMapLatest {
        it.toPath().listDirectoryEntriesFlow("*")
    }
    .map { items -> items.map { it.toFile() } }
    .mapLatest { items ->
        val visibleFiles = items.filter {  showHidden.value || !it.isHidden }
        val dirs = visibleFiles.filter { it.isDirectory }.map { FileObjectInfo(it) }.sortedByDescending { it.lastModified }
        val files = visibleFiles.filter { it.isFile }.map { FileObjectInfo(it) }.sortedByDescending { it.lastModified }
        val parent = items.firstOrNull()?.parentFile?.parentFile?.let { listOf(ParentObjectInfo(it.absolutePath)) } ?: emptyList()
        parent + dirs + files
    }
    .stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())

    fun openObject(dataObjectInfo: DataObjectInfo) {
        when (dataObjectInfo) {
            is ParentObjectInfo -> openDir(File(dataObjectInfo.id))
            is FileObjectInfo -> if (dataObjectInfo.isDir) {
                openDir(dataObjectInfo.file)
            } else {
                Desktop.getDesktop().open(dataObjectInfo.file)
            }
        }
    }

    fun createDir() {

    }

    private fun openDir(file: File) {
        screenModelScope.launch {
            setCurrentDir.setCurrentDir(file)
        }
        currentDir.update { file }
    }

    fun onRemove(dataObjectInfo: DataObjectInfo) {
        when (dataObjectInfo) {
            is  FileObjectInfo -> Desktop.getDesktop().moveToTrash(dataObjectInfo.file)
        }
    }
}