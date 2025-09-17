package nl.rio282.simple_lrc_creator_visual.component

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onImportMp3: () -> Unit,
    onImportLrc: () -> Unit,
    onExportLrc: () -> Unit,
    onExit: () -> Unit,
    readyToImportLrc: Boolean,
    readyToExportLrc: Boolean,
) {
    TopAppBar(
        title = { Text("Simple LRC Creator") },
        actions = {
            TextButton(onClick = onImportMp3) { Text("Import MP3") }
            TextButton(
                onClick = onImportLrc,
                enabled = readyToImportLrc
            ) {
                Text("Import LRC")
            }
            TextButton(
                onClick = onExportLrc,
                enabled = readyToExportLrc
            ) {
                Text("Export LRC")
            }
            TextButton(onClick = onExit) { Text("Exit") }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
