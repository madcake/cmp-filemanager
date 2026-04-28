package ru.madcake.filemanager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.KoinContext
import ru.madcake.filemanager.designsystem.components.newImageLoader
import ru.madcake.filemanager.designsystem.theme.AppTheme
import ru.madcake.filemanager.features.main.presents.MainScreen

@OptIn(ExperimentalCoilApi::class)
@Composable
fun App() {
    AppTheme {
        KoinContext {
            setSingletonImageLoaderFactory { context ->
                newImageLoader(context, true)
            }
            Navigator(MainScreen()) {
                SlideTransition(it)
            }
        }
    }
}

@Composable
fun Apps() {
    var showContent by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { showContent = !showContent }) {
            Text("Click me!")
        }
        AnimatedVisibility(showContent) {
            val greeting = remember { Greeting().greet() }
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painterResource(Res.drawable.compose_multiplatform), null)
                Text("Compose: $greeting")
            }
        }
    }
}

object AppKeyEvent {
    private var listener: (event: KeyEvent) -> Boolean = { false }
    private var tabListener: () -> Unit = { }

    internal fun onKeyPreview(event: KeyEvent): Boolean {
        if (event.type == KeyEventType.KeyUp) {
            when {
                event.key == Key.Tab -> tabListener()
            }
        }

        return listener.invoke(event)
    }

    fun registerListener(listener: (KeyEvent) -> Boolean) {
        this.listener = listener
    }

    fun onTabListener(listener: () -> Unit) {
        tabListener = listener
    }
}