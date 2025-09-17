package nl.rio282.simple_lrc_creator_visual.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nl.rio282.simple_lrc_creator_visual.component.Mp3InfoComponent
import nl.rio282.simple_lrc_creator_visual.component.Mp3WaveformComponent
import nl.rio282.simple_lrc_creator_visual.component.TopBar
import nl.rio282.simple_lrc_creator_visual.controller.LrcController
import nl.rio282.simple_lrc_creator_visual.controller.Mp3Controller
import nl.rio282.simple_lrc_creator_visual.model.LyricLine
import nl.rio282.simple_lrc_creator_visual.model.Mp3Model
import kotlin.system.exitProcess

@Composable
fun MainView() {
    var mp3 by remember { mutableStateOf<Mp3Model?>(null) }
    var lrcLoaded by remember { mutableStateOf(false) }
    var displayFileInfo by remember { mutableStateOf(false) }

    var currentPositionMs by remember { mutableStateOf(0L) }
    var currentStep by remember { mutableStateOf(0) }
    val maxSteps = 500
    val lyrics = remember { mutableStateListOf<LyricLine>() } // start ms of lyric, lyric text
    var currentLyric by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(
                onImportMp3 = {
                    mp3 = Mp3Controller.pickMp3File() ?: mp3
                },
                onImportLrc = {
                    val lrcFile = LrcController.pickLrcFile() ?: return@TopBar

                    lyrics.clear()
                    lyrics.addAll(LrcController.importLrc(lrcFile))

                    lrcLoaded = true
                },
                onExportLrc = {
                    val exportFile = LrcController.pickSaveLocationJFC() ?: return@TopBar
                    if (!exportFile.exists()) exportFile.createNewFile()
                    exportFile.writeText(LrcController.exportToLrc(lyrics))
                },
                onExit = { exitProcess(0) },
                readyToImportLrc = mp3 != null,
                readyToExportLrc = mp3 != null && lyrics.count() > 0,
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
                        Button(
                            onClick = { displayFileInfo = !displayFileInfo },
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(
                                start = 8.dp,
                                top = 4.dp,
                                end = 8.dp,
                                bottom = 4.dp
                            )
                        ) {
                            Text("Show File Information", maxLines = 1)
                        }

                        if (displayFileInfo) {
                            Mp3InfoComponent(mp3!!)
                        }

                        Mp3WaveformComponent(
                            mp3 = mp3!!,
                            currentPositionMs = currentPositionMs,
                            currentStep = currentStep,
                            maxSteps = maxSteps,
                            onPositionChange = {
                                currentPositionMs = it
                                currentStep = ((it.toDouble() / mp3!!.durationMs) * maxSteps).toInt()
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Timestamp: ${Mp3Controller.formatTimeMs(currentPositionMs)} / ${
                                Mp3Controller.formatTimeMs(
                                    mp3!!.durationMs
                                )
                            }"
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(2f)
                                    .fillMaxHeight()
                            ) {
                                OutlinedTextField(
                                    value = currentLyric,
                                    onValueChange = { currentLyric = it },
                                    label = { Text("Lyrics") },
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                                    .padding(4.dp)
                            ) {
                                items(lyrics) { line ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                currentPositionMs = line.timestampMs
                                                currentStep =
                                                    ((line.timestampMs.toDouble() / mp3!!.durationMs) * maxSteps).toInt()
                                                currentLyric = line.text
                                            }
                                            .padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = Mp3Controller.formatTimeMs(line.timestampMs),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.width(60.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = line.text.take(20),
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }


                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                lyrics.add(LyricLine(currentPositionMs, currentLyric))
                                lyrics.sortBy { it.timestampMs }
                                currentLyric = ""
                            },
                        ) {
                            Text("+ Add Lyric")
                        }

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
