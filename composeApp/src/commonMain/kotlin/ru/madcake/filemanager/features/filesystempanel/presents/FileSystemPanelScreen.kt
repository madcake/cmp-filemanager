package ru.madcake.filemanager.features.filesystempanel.presents

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import ru.madcake.filemanager.designsystem.components.items.DataObjectInfo
import ru.madcake.filemanager.designsystem.components.list.DataObjectList
import ru.madcake.filemanager.designsystem.theme.padding16
import ru.madcake.filemanager.features.filesystempanel.viewmodels.FileSystemPanelViewModel

class FileSystemPanelScreen(
    private val onPanelClick: () -> Unit,
    private val onSelect: (DataObjectInfo?) -> Unit,
    private val onCopy: (DataObjectInfo) -> Unit,
    private val onMove: (DataObjectInfo) -> Unit,
) : Screen {

    var isActive = false

    @Composable
    override fun Content() {
        val viewModel: FileSystemPanelViewModel = getScreenModel()
        val files by viewModel.filesInCurrentDir.collectAsState()
        val container by viewModel.container.collectAsState()

        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier.padding(padding16)
                ) {
                    Text(
                        modifier = Modifier.weight(0.7f),
                        text = container?.id ?: "",
                        overflow = TextOverflow.MiddleEllipsis,
                        softWrap = false,
                        maxLines = 1,
                    )
                }
            }
        ) { innerPadding ->
            DataObjectList(
                modifier = Modifier.padding(innerPadding),
                container = container,
                isActive = isActive,
                dataObjects = files,
                onPanelClick = onPanelClick,
                onCopy = onCopy,
                onMove = onMove,
                onOpen = viewModel::openObject,
                onCreateDir = viewModel::createDir,
                onSelect = onSelect,
                onRemove = {
                    viewModel.onRemove(it)
                    onSelect(null)
                },
            )
        }
    }

    companion object {
        const val panelId = "filePanel"
    }
}



