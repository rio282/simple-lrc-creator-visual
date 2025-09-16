package nl.rio282.simple_lrc_creator_visual.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.rio282.simple_lrc_creator_visual.model.Mp3Model

@Composable
fun Mp3InfoView(mp3: Mp3Model) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("File: ${mp3.file.name}")
        Text("Path: ${mp3.file.absolutePath}")
        Text("Title: ${mp3.title ?: "Unknown"}")
        Text("Artist: ${mp3.artist ?: "Unknown"}")
        Text("Album: ${mp3.album ?: "Unknown"}")
        Text("Duration: ${mp3.durationMs} seconds")
    }
}

