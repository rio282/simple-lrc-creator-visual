package nl.rio282.simple_lrc_creator_visual.controller

import nl.rio282.simple_lrc_creator_visual.model.LyricLine
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

object LrcController {

    fun pickLrcFile(): File? {
        val chooser = JFileChooser().apply {
            dialogTitle = "Select an LRC File"
            fileFilter = FileNameExtensionFilter("LRC files", "lrc")
            isAcceptAllFileFilterUsed = false
        }
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return null
        return chooser.selectedFile
    }

    fun importLrc(file: File): List<LyricLine> {
        val lines = mutableListOf<LyricLine>()

        val timestampStringLength = 10
        val regex = Regex("""^\[\d{2}:\d{2}\.\d{2}]""")

        file.forEachLine { line ->
            if (regex.containsMatchIn(line)) {
                val timestamp: Long = timestampToMs(line.take(timestampStringLength))
                val text: String = line.drop(timestampStringLength)
                lines.add(LyricLine(timestamp, text))
            }
        }

        return lines.toList()
    }

    fun exportToLrc(lines: List<LyricLine>): String {
        return lines.sortedBy { it.timestampMs }
            .joinToString("\n") { line ->
                formatTimestamp(line.timestampMs)
            }
    }

    fun formatTimestamp(ms: Long): String {
        val minutes = (ms / 60000)
        val seconds = (ms % 60000) / 1000
        val hundredths = (ms % 1000) / 10
        return "%02d:%02d.%02d".format(minutes, seconds, hundredths)
    }

    fun timestampToMs(timestamp: String): Long {
        val clean = timestamp.trim().removePrefix("[").removeSuffix("]")
        val parts = clean.split(":", ".")

        if (parts.size < 3) throw IllegalArgumentException("Invalid timestamp format: $timestamp")

        val minutes = parts[0].toLong()
        val seconds = parts[1].toLong()
        val hundredths = parts[2].toLong()

        return minutes * 60_000 + seconds * 1_000 + hundredths * 10
    }

}