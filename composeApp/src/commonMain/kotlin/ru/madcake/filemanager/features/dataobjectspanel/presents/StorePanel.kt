package ru.madcake.filemanager.features.dataobjectspanel.presents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import ru.madcake.filemanager.core.domain.BranchType
import ru.madcake.filemanager.designsystem.components.items.DataObjectInfo
import ru.madcake.filemanager.designsystem.components.list.DataObjectList
import ru.madcake.filemanager.designsystem.theme.Spacer16
import ru.madcake.filemanager.designsystem.theme.padding16
import ru.madcake.filemanager.features.dataobjectspanel.viewmodels.StoreViewModel

class StorePanel(
    private val onPanelClick: () -> Unit,
    private val onSelect: (DataObjectInfo?) -> Unit,
    private val onCopy: (DataObjectInfo) -> Unit,
    private val onMove: (DataObjectInfo) -> Unit,
    private val onOpenDir: (DataObjectInfo) -> Unit,
) : Screen {

    var isActive: Boolean = false

    @Composable
    override fun Content() {
        val viewModel: StoreViewModel = getScreenModel()
        val items by viewModel.branch.collectAsState()

        var isCreateDialog by remember { mutableStateOf(false) }

        DataObjectList(
            container = null,
            items,
            isActive = isActive,
            isFocused = !isCreateDialog,
            onPanelClick = onPanelClick,
            onCopy = onCopy,
            onMove = onMove,
            onCreateDir = { isCreateDialog = true },
            onSelect = onSelect,
            onOpen = { viewModel.open(it, onOpenDir) },
            onRemove = { viewModel.remove(it) },
        )
        if (isCreateDialog) {
            CreateBranchDialog(
                onDismiss = { isCreateDialog = false },
                onCreate = viewModel::createDir
            )
        }
    }

    companion object {
        const val PANEL_ID: String = "storePanel"
    }
}

@Composable
private fun CreateBranchDialog(
    onDismiss: () -> Unit,
    onCreate: (String, BranchType) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(padding16),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create branch",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer16()
                Box {
                    BasicTextField(
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                        value = name, // TODO: Add full path
                        onValueChange = { name = it },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onCreate(name, BranchType.Dir)
                                onDismiss()
                            }
                        )
                    )
                    if (name.isEmpty()) {
                        Text(
                            "Name",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}