package ru.madcake.filemanager.designsystem.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun Icon(
    imageVector: ImageVector,
    tint: Color = LocalContentColor.current,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.Icon(
        modifier = modifier,
        imageVector = imageVector,
        contentDescription = imageVector.name,
        tint = tint,
    )
}

@Composable
fun Icon(
    imageBitmap: ImageBitmap,
    tint: Color = LocalContentColor.current,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.Icon(
        modifier = modifier,
        bitmap = imageBitmap,
        contentDescription = "",
        tint = tint,
    )
}