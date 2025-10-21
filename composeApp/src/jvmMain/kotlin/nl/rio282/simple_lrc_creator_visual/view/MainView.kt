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
import nl.rio282.simple_lrc_creator_visual.model.LrcModel
import nl.rio282.simple_lrc_creator_visual.model.LyricLine
import nl.rio282.simple_lrc_creator_visual.model.Mp3Model
import kotlin.system.exitProcess

@Composable
fun MainView() {
    var mp3 by remember { mutableStateOf<Mp3Model?>(null) }
    var lrc by remember { mutableStateOf(LrcModel()) }
    var displayFileInfo by remember { mutableStateOf(false) }

    val lyrics = remember { mutableStateListOf<LyricLine>() }
    var currentMs by remember { mutableStateOf(0L) }

    Scaffold(
        topBar = {
            TopBar(
                onImportMp3 = {
                    mp3 = Mp3Controller.pickMp3File() ?: mp3
                },
                onImportLrc = {
                    val lrcFile = LrcController.pickLrcFile() ?: return@TopBar
                    lrc.loaded = false

                    lyrics.clear()
                    lyrics.addAll(LrcController.importLrc(lrcFile))

                    lrc.loaded = true
                },
                onExportLrc = {
                    val exportFile = LrcController.pickSaveLocationJFC() ?: return@TopBar
                    if (!exportFile.exists()) exportFile.createNewFile()
                    exportFile.writeText(LrcController.exportToLrc(lyrics, mp3!!.durationMs))
                },
                onEmbedLyircsIntoMp3 = {
                    LrcController.embedLyrics(
                        mp3Model = mp3!!,
                        lyrics = LrcController.exportToLrc(lyrics, mp3!!.durationMs)
                    )
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
                            onPositionChange = {
                                currentMs = it
                            }
                        )
                         Spacer(modifier = Modifier.height(16.dp))
//                        Text(
//                            "Timestamp: ${Mp3Controller.formatTimeMs(currentLyric?.timestampMs ?: -1)} / ${
//                                Mp3Controller.formatTimeMs(
//                                    mp3!!.durationMs
//                                )
//                            }"
//                        )
//                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        ) {
//                            LazyColumn(
//                                modifier = Modifier
//                                    .weight(1f)
//                                    .fillMaxHeight()
//                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
//                                    .padding(4.dp)
//                            ) {
//                                items(lyrics) { line ->
//                                    Row(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .clickable {
//                                                // ((line.timestampMs.toDouble() / mp3!!.durationMs) * maxSteps).toInt()
////                                                currentMs =
//                                            }
//                                            .padding(vertical = 2.dp),
//                                        verticalAlignment = Alignment.CenterVertically
//                                    ) {
//                                        Text(
//                                            text = Mp3Controller.formatTimeMs(line.timestampMs),
//                                            style = MaterialTheme.typography.bodySmall,
//                                            fontWeight = FontWeight.Bold,
//                                            modifier = Modifier.width(60.dp)
//                                        )
//                                        Spacer(modifier = Modifier.width(2.dp))
//                                        Text(
//                                            text = line.text.take(20),
//                                            style = MaterialTheme.typography.bodyMedium,
//                                            maxLines = 1
//                                        )
//                                    }
//                                }
//                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                // TODO: open small window with textbox
                            },
                        ) {
                            Text("+ Add Lyric")
                        }

                        if (!lrc.loaded) {
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
