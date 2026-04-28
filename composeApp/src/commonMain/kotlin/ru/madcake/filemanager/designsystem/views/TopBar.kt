package ru.madcake.filemanager.designsystem.theme

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopBar(
    title: StringResource,
    onCancel: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopBar(
        title = stringResource(title),
        onCancel = onCancel,
        actions = actions,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    onCancel: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val navigator = LocalNavigator.currentOrThrow

    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            if (onCancel != null) {
                IconButton(onClick = onCancel) {
                    Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack)
                }
            }
        },
        actions = actions,
    )
}