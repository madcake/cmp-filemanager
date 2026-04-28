package ru.madcake.filemanager.designsystem.theme

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val padding4 = 4.dp
val padding8 = 8.dp
val padding16 = 16.dp
val padding32 = 32.dp

val space2 = 2.dp
val space4 = 4.dp
val space8 = 8.dp
val space16 = 16.dp

@Composable
fun Spacer2() {
    Spacer(modifier = Modifier.size(space2))
}

@Composable
fun Spacer4() {
    Spacer(modifier = Modifier.size(space4))
}

@Composable
fun Spacer8() {
    Spacer( modifier = Modifier.size(space8))
}

@Composable
fun Spacer16() {
    Spacer(modifier = Modifier.size(space16))
}