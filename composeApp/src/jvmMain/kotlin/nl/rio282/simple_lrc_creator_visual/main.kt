package nl.rio282.simple_lrc_creator_visual

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "simple-lrc-creator-visual",
    ) {
        App()
    }
}