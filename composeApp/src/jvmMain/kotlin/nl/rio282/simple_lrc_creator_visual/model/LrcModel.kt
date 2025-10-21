package nl.rio282.simple_lrc_creator_visual.model

data class LrcModel(
    val lyrics: MutableList<LyricLine> = mutableListOf(),
    var loaded: Boolean = false,
    var fromFile: Boolean = false,
);