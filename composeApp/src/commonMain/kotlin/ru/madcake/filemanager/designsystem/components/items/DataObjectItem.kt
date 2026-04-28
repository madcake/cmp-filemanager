package ru.madcake.filemanager.designsystem.components.items

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.annotation.InternalCoilApi
import coil3.compose.AsyncImage
import coil3.util.MimeTypeMap
import ru.madcake.filemanager.core.domain.BranchMeta
import ru.madcake.filemanager.core.domain.DataObjectMeta
import ru.madcake.filemanager.core.domain.DataObjectType
import ru.madcake.filemanager.designsystem.theme.Icon
import ru.madcake.filemanager.designsystem.theme.Spacer4
import ru.madcake.filemanager.designsystem.theme.padding8
import ru.madcake.filemanager.workspaceDir
import java.io.File

/* TODO: On think:
 * interface DataObjectItem {
 *      @Composable
 *      fun View() {}
 *
 *      interface Model { }
 * }
 */

@Composable
fun DataObjectItemView(
    item: DataObjectInfo,
    isSelected: Boolean,
    isActive: Boolean,
    onClick: () -> Unit,
    onOpen: (DataObjectInfo) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .indication(interactionSource, null)
            .hoverable(interactionSource, false)
            .background(fileItemBackground(isActive, isSelected))
            .fillMaxWidth()
            .indication(interactionSource, null)
            .combinedClickable(
                indication = null,
                interactionSource = interactionSource,
                onDoubleClick = { onOpen(item) },
                onClick = onClick
            )
            .padding(padding8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (item.contentType) {
            DataObjectType.None -> Icon(
                modifier = Modifier.size(32.dp),
                imageVector = item.icon,
                tint = fileItemIconForeground(isActive, isSelected),
            )
            DataObjectType.Image -> AsyncImage(
                modifier = Modifier.size(32.dp).clip(MaterialTheme.shapes.extraSmall),
                model = item.previewModel,
                contentScale = ContentScale.FillBounds,
                contentDescription = ""
            )
        }
        Spacer4()
        Text(
            modifier = Modifier.weight(1f),
            text = item.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = fileItemTextForeground(isActive, isSelected)
        )
    }
}

interface DataObjectInfo {
    val icon: ImageVector
    val name: String
    val isDir: Boolean
    val contentType: DataObjectType
    val previewModel: Any
}

data class ParentObjectInfo(val id: String) : DataObjectInfo {
    override val icon: ImageVector
        get() = Icons.Default.FolderOpen

    override val name: String
        get() = ".."

    override val isDir: Boolean
        get() = true

    override val contentType: DataObjectType
        get() = DataObjectType.None

    override val previewModel: Any
        get() = throw Exception("Illegal access")
}

@OptIn(InternalCoilApi::class)
data class FileObjectInfo(
    val file: File,
) : DataObjectInfo, Comparable<FileObjectInfo> {

    override val icon: ImageVector
        get() = if (isDir) Icons.Default.Folder else Icons.Default.Description

    override val isDir: Boolean
        get() = file.isDirectory

    override val contentType: DataObjectType
        get() {
            val mimeType = MimeTypeMap.getMimeTypeFromExtension(file.extension) // TODO: Simplify out image type
            return when (mimeType?.split("/")?.firstOrNull()) {
                "image" -> DataObjectType.Image
                else -> DataObjectType.None
            }
    }

    override val previewModel: Any
        get() = file

    val lastModified by lazy { file.lastModified() }

    override val name: String by lazy { file.name }

    override fun compareTo(other: FileObjectInfo): Int {
        return when {
            isDir && !other.isDir -> -1
            !isDir && other.isDir -> 1
            else -> (lastModified - other.lastModified).toInt()
        }
    }
}

data class StoreObjectInfo(
    val dataObjectMeta: DataObjectMeta,
) : DataObjectInfo {

    override val icon: ImageVector = Icons.Default.FileOpen

    override val isDir: Boolean
        get() = false

    val lastModified = dataObjectMeta.createdAt

    override val name: String = dataObjectMeta.name

    override val contentType: DataObjectType
        get() = dataObjectMeta.contentType

    override val previewModel: Any
        get() = File(File(workspaceDir(), "objects"), dataObjectMeta.hash) // TODO: Build it in repository

    val file: File
        get() = File(File(workspaceDir(), "objects"), dataObjectMeta.hash)
}

data class StoreBranchInfo(
    val branchMeta: BranchMeta,
) : DataObjectInfo {

    override val icon: ImageVector = Icons.Default.Folder

    override val isDir: Boolean
        get() = false

    val lastModified = branchMeta.createdAt

    override val name: String = branchMeta.name

    override val contentType: DataObjectType
        get() = DataObjectType.None

    override val previewModel: Any
        get() = ""
}

@Composable
private fun fileItemBackground(isActive: Boolean, isSelected: Boolean): Color {
    return if (isSelected) {
        if (isActive)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.background
    }
}

@Composable
private fun fileItemTextForeground(isActive: Boolean, isSelected: Boolean): Color {
    return if (isSelected) {
        if (isActive)
            MaterialTheme.colorScheme.onPrimary
        else
            MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onBackground
    }
}

@Composable
private fun fileItemIconForeground(isActive: Boolean, isSelected: Boolean): Color {
    return if (isSelected) {
        if (isActive)
            MaterialTheme.colorScheme.onPrimary
        else
            MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.primary
    }
}