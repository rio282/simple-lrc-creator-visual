package nl.rio282.simple_lrc_creator_visual.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.rio282.simple_lrc_creator_visual.controller.Mp3Controller
import nl.rio282.simple_lrc_creator_visual.model.Mp3Model
import javax.swing.JOptionPane
import kotlin.system.exitProcess

@Composable
fun MainView() {
    var mp3 by remember { mutableStateOf<Mp3Model?>(null) }
    var lrcLoaded by remember { mutableStateOf(false) }
    var currentPositionMs by remember { mutableStateOf(0L) }
    var lyrics by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(
                onImportMp3 = {
                    mp3 = Mp3Controller.pickMp3File()
                },
                onImportLrc = { lrcLoaded = true },
                onExportLrc = {
                    JOptionPane.showMessageDialog(
                        null,
                        "WORK IN PROGRESS",
                        "Export LRC",
                        JOptionPane.INFORMATION_MESSAGE
                    )
                },
                onExit = { exitProcess(0) }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                mp3 == null -> {
                    Text("Please import an MP3 file via File -> Import MP3")
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Mp3WaveformView(
                            mp3 = mp3!!,
                            currentPositionMs = currentPositionMs,
                            onPositionChange = { currentPositionMs = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Timestamp: ${Mp3Controller.formatTimeMs(currentPositionMs)} / " +
                                    Mp3Controller.formatTimeMs(mp3!!.durationMs)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = lyrics,
                            onValueChange = { lyrics = it },
                            label = { Text("Lyrics") },
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(150.dp)
                        )

                        if (!lrcLoaded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No LRC file imported",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
