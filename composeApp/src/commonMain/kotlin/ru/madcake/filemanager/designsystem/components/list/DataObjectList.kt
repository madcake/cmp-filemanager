package ru.madcake.filemanager.designsystem.components.list

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import kotlinx.coroutines.launch
import ru.madcake.filemanager.AppKeyEvent
import ru.madcake.filemanager.designsystem.components.items.DataObjectInfo
import ru.madcake.filemanager.designsystem.components.items.DataObjectItemView
import kotlin.math.max
import kotlin.math.min

@Composable
fun DataObjectList(
    container: DataObjectInfo?,
    dataObjects: List<DataObjectInfo>,
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    isFocused: Boolean = true,
    onPanelClick: () -> Unit,
    onCopy: (DataObjectInfo) -> Unit,
    onMove: (DataObjectInfo) -> Unit,
    onCreateDir: () -> Unit,
    onOpen: (DataObjectInfo) -> Unit,
    onSelect: (DataObjectInfo?) -> Unit,
    onRemove: (DataObjectInfo) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val interactionSource = remember { MutableInteractionSource() }

    var selectedIndex by remember { mutableStateOf(0) }
    var selectDirection by remember { mutableStateOf(0) }

    LazyColumn (
        modifier = modifier
            .fillMaxSize()
            .combinedClickable(
                indication = null,
                interactionSource = interactionSource,
                onClick = onPanelClick
            )
            .background(color = MaterialTheme.colorScheme.background),
        state = listState
    ) {
        files(
            dataObjects,
            selectedIndex,
            isActive,
            {
                onPanelClick()
                selectedIndex = it
            }
        ) { item -> onOpen(item) }
    }

    dataObjectsKeyEvenHandle(
        isActive,
        { isFocused },
        onSelect = { direction, type ->
            selectDirection = direction.offset
            selectedIndex = when (type) {
                SelectType.Item -> dataObjects.getSelectedIndex(selectedIndex, selectDirection)
                SelectType.Edge -> when (direction) {
                    SelectDirection.Up -> 0
                    SelectDirection.Down -> dataObjects.size - 1
                }
                SelectType.Page -> {
                    when (direction) {
                        SelectDirection.Up -> {
                            val pageSize = listState.layoutInfo.visibleItemsInfo
                                .filter { it.offset > 0 && it.offset < listState.layoutInfo.viewportSize.height }
                                .size - 2
                            max(0, selectedIndex - pageSize)
                        }
                        SelectDirection.Down -> {
                            val pageSize = listState.layoutInfo.visibleItemsInfo
                                .filter { it.offset > 0 && it.offset < listState.layoutInfo.viewportSize.height }
                                .size - 2
                            min(dataObjects.size - 1, selectedIndex + pageSize)
                        }
                    }
                }
            }
        },
        onCopy = { dataObjects.getOrNull(selectedIndex)?.let(onCopy) },
        onMove = { dataObjects.getOrNull(selectedIndex)?.let(onMove) },
        onCreateDir = onCreateDir,
        onOpen = { dataObjects.openObject(selectedIndex, onOpen) },
        onRemove = { dataObjects.getOrNull(selectedIndex)?.let(onRemove) }
    )

    LaunchedEffect(container) {
        selectedIndex = 0
    }

    LaunchedEffect(dataObjects, selectedIndex) {
        scope.launch {
            onSelect(dataObjects.getOrNull(selectedIndex))
            val layoutInfo = listState.layoutInfo
            val viewPortSize = layoutInfo.viewportSize
            val selectedItem = layoutInfo.visibleItemsInfo.firstOrNull { it.index == selectedIndex }
            when  {
                selectedItem == null -> {
                    val previous = layoutInfo.visibleItemsInfo.firstOrNull { it.index == selectedIndex + selectDirection}
                    if (previous == null) {
                        listState.scrollToItem(max(selectedIndex, 0))
                    } else {
                        val offset = if (selectDirection > 0) {
                            (previous.size + previous.offset) - viewPortSize.height
                        } else {
                            -(previous.offset + previous.size)
                        }
                        listState.animateScrollBy(offset.toFloat())
                    }
                }
                else -> {
                    val offset = if (selectDirection > 0) {
                        val next = layoutInfo.visibleItemsInfo.firstOrNull { it.index == selectedIndex + 1 }?.let {
                            it.offset + it.size
                        } ?: 0
                        max(next + selectedItem.size - viewPortSize.height, 0)
                    } else {
                        min(selectedItem.offset, 0)
                    }
                    listState.animateScrollBy(offset.toFloat())
                }
            }
        }
    }
}

fun <T: DataObjectInfo> LazyListScope.files(
    files: List<T>,
    selectedIndex: Int,
    isActive: Boolean,
    onClick: (Int) -> Unit,
    onOpen: (DataObjectInfo) -> Unit,
) {
    itemsIndexed(files) { index, item ->
        DataObjectItemView(item, selectedIndex == index, isActive, { onClick(index) }, onOpen)
    }
}

private fun List<DataObjectInfo>.getSelectedIndex(current: Int, offset: Int): Int {
    return max(0, min(current + offset, size - 1))
}

private fun List<DataObjectInfo>.openObject(
    selectedIndex: Int,
    onOpen: (DataObjectInfo) -> Unit,
): Boolean {
    onOpen(getOrNull(selectedIndex) ?: return false)
    return true
}

private fun dataObjectsKeyEvenHandle(
    isActive: Boolean,
    isFocused: () -> Boolean,
    onSelect: (SelectDirection, SelectType) -> Unit,
    onOpen: () -> Unit,
    onCopy: () -> Unit,
    onMove: () -> Unit,
    onCreateDir: () -> Unit,
    onRemove: () -> Unit,
) {
    if (!isActive) return

    AppKeyEvent.registerListener { event ->
        if (event.type != KeyEventType.KeyDown || !isFocused()) {
            return@registerListener false
        }
        when {
            event.key == Key.DirectionLeft ||
            event.key == Key.DirectionUp -> onSelect(SelectDirection.Up, SelectType.Item)
            event.key == Key.DirectionRight ||
            event.key == Key.DirectionDown -> onSelect(SelectDirection.Down, SelectType.Item)
            event.key == Key.PageUp -> onSelect(SelectDirection.Up, SelectType.Page)
            event.key == Key.PageDown -> onSelect(SelectDirection.Down, SelectType.Page)
            event.key == Key.Home -> onSelect(SelectDirection.Up, SelectType.Edge)
            event.key == Key.MoveEnd -> onSelect(SelectDirection.Down, SelectType.Edge)
            event.key == Key.Enter -> onOpen()
            event.key == Key.F5 -> onCopy()
            event.key == Key.F6 -> onMove()
            event.key == Key.F7 -> onCreateDir()
            event.isMetaPressed && event.key == Key.Backspace -> onRemove()
            else -> return@registerListener false
        }
        true
    }
}

private enum class SelectDirection(val offset: Int) {
    Up(-1),
    Down(1),
}

private enum class SelectType {
    Item,
    Edge,
    Page
}