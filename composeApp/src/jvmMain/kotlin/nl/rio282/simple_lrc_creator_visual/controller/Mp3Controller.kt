package nl.rio282.simple_lrc_creator_visual.controller

import com.mpatric.mp3agic.Mp3File
import javazoom.jl.decoder.Bitstream
import javazoom.jl.decoder.Decoder
import javazoom.jl.decoder.SampleBuffer
import nl.rio282.simple_lrc_creator_visual.model.Mp3Model
import java.io.File
import java.io.FileInputStream
import javax.swing.JFileChooser

object Mp3Controller {

    fun pickMp3File(): Mp3Model? {
        val chooser = JFileChooser().apply { dialogTitle = "Select an MP3 File" }
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return null
        val file = chooser.selectedFile

        // decode MP3 to PCM samples with jlayer
        val samples = decodeToPCM(file)

        // metadata with mp3agic
        val mp3agicFile = Mp3File(file)
        val (title, artist, album) = when {
            mp3agicFile.hasId3v2Tag() -> {
                val tag = mp3agicFile.id3v2Tag
                Triple(tag.title, tag.artist, tag.album)
            }

            mp3agicFile.hasId3v1Tag() -> {
                val tag = mp3agicFile.id3v1Tag
                Triple(tag.title, tag.artist, tag.album)
            }

            else -> Triple(null, null, null)
        }

        return Mp3Model(
            file = file,
            title = title,
            artist = artist,
            album = album,
            durationMs = mp3agicFile.lengthInMilliseconds,
            pcmSamples = samples
        )
    }

    private fun decodeToPCM(file: File): ShortArray {
        val fis = FileInputStream(file)
        val bitstream = Bitstream(fis)
        val decoder = Decoder()

        val pcmList = mutableListOf<Short>()

        var header = bitstream.readFrame()
        while (header != null) {
            val output = decoder.decodeFrame(header, bitstream) as SampleBuffer
            val buffer = output.buffer
            val length = output.bufferLength

            for (i in 0 until length) {
                pcmList.add(buffer[i].toShort())
            }

            bitstream.closeFrame()
            header = bitstream.readFrame()
        }

        fis.close()
        return pcmList.toShortArray()
    }

    fun formatTimeMs(ms: Long): String {
        val minutes = (ms / 60000)
        val seconds = (ms % 60000) / 1000
        val hundredths = (ms % 1000) / 10
        return "%02d:%02d.%02d".format(minutes, seconds, hundredths)
    }
}
