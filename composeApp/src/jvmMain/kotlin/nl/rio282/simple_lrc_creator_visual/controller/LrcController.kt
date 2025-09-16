package nl.rio282.simple_lrc_creator_visual.controller

import nl.rio282.simple_lrc_creator_visual.model.LyricLine

object LrcController {

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

}