package nl.rio282.simple_lrc_creator_visual.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import nl.rio282.simple_lrc_creator_visual.controller.Mp3Controller
import nl.rio282.simple_lrc_creator_visual.model.Mp3Model
import kotlin.math.roundToInt

@Composable
fun Mp3WaveformView(
    mp3: Mp3Model,
    currentPositionMs: Long,
    onPositionChange: (Long) -> Unit
) {
    // precompute waveform samples (reduce to ~500 points for display)
    val samples = remember(mp3) {
        val step = (mp3.pcmSamples.size / 500.0).coerceAtLeast(1.0)
        List(500) { i ->
            val index = (i * step).roundToInt().coerceAtMost(mp3.pcmSamples.size - 1)
            mp3.pcmSamples[index] / Short.MAX_VALUE.toFloat()
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            val widthPerSample = size.width / samples.size
            val centerY = size.height / 2
            for ((i, sample) in samples.withIndex()) {
                val x = i * widthPerSample
                val y = sample * centerY
                drawLine(
                    color = androidx.compose.ui.graphics.Color.Blue,
                    start = Offset(x, centerY - y),
                    end = Offset(x, centerY + y),
                    strokeWidth = 1f
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = currentPositionMs.toFloat(),
            onValueChange = { onPositionChange(it.toLong()) },
            valueRange = 0f..mp3.durationMs.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            "Time: ${Mp3Controller.formatTimeMs(currentPositionMs)} / " +
                    Mp3Controller.formatTimeMs(mp3.durationMs)
        )
    }
}
