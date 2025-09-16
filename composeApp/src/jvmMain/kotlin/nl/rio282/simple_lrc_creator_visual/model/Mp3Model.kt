package nl.rio282.simple_lrc_creator_visual.model

import java.io.File

data class Mp3Model(
    val file: File,
    val title: String?,
    val artist: String?,
    val album: String?,
    val durationMs: Long,
    val pcmSamples: ShortArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Mp3Model

        if (durationMs != other.durationMs) return false
        if (file != other.file) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (album != other.album) return false
        if (!pcmSamples.contentEquals(other.pcmSamples)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = durationMs.hashCode()
        result = 31 * result + file.hashCode()
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (artist?.hashCode() ?: 0)
        result = 31 * result + (album?.hashCode() ?: 0)
        result = 31 * result + pcmSamples.contentHashCode()
        return result
    }
}
