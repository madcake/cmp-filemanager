package ru.madcake.filemanager

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.startKoin
import ru.madcake.filemanager.di.mainModule

fun main() = application {
    startKoin {
        modules(mainModule)
    }
    Window(
        onPreviewKeyEvent = AppKeyEvent::onKeyPreview,
        onCloseRequest = ::exitApplication,
        title = "KotlinProject",
    ) {
        App()
    }
}

