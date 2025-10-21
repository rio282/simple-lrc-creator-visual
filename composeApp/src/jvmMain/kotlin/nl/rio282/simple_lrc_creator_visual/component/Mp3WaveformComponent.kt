package nl.rio282.simple_lrc_creator_visual.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceAtMost
import androidx.compose.ui.util.fastRoundToInt
import nl.rio282.simple_lrc_creator_visual.controller.Mp3Controller
import nl.rio282.simple_lrc_creator_visual.model.Mp3Model

@Composable
fun Mp3WaveformComponent(
    mp3: Mp3Model,
    onPositionChange: (Long) -> Unit,
    waveformPeaksLimit: Long = 500L
) {
    val maxSteps = mp3.durationMs.coerceAtMost(waveformPeaksLimit)
    var currentMs by remember { mutableLongStateOf(0L) }
    var currentStep by remember { mutableLongStateOf(0L) }

    fun onPositionChangeWrapper(newMs: Long) {
        currentMs = newMs
        currentStep = ((newMs.toDouble() / mp3.durationMs) * maxSteps).toLong()
        onPositionChange(newMs)
    }

    // precompute waveform samples (reduce to ~maxSteps points for display)
    val samples = remember(mp3) {
        val step = (mp3.pcmSamples.size / maxSteps.toDouble()).coerceAtLeast(1.0)
        List(maxSteps.toInt()) { i ->
            val index = (i * step).fastRoundToInt().fastCoerceAtMost(mp3.pcmSamples.size - 1)
            mp3.pcmSamples[index] / Short.MAX_VALUE.toFloat()
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val newStep = (offset.x / size.width * maxSteps).toInt()
                        val newPosition = (newStep.toDouble() / maxSteps * mp3.durationMs).toLong()
                        onPositionChangeWrapper(newPosition)
                    }
                }
        ) {
            val widthPerSample = size.width / samples.size
            val centerY = size.height / 2
            for ((i, sample) in samples.withIndex()) {
                val x = i * widthPerSample
                val y = sample * centerY
                drawLine(
                    color = if (i < currentStep) Color.Blue else Color.Gray,
                    start = Offset(x, centerY - y),
                    end = Offset(x, centerY + y),
                    strokeWidth = 1f
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = currentMs.toFloat(),
            onValueChange = { onPositionChangeWrapper(it.toLong()) },
            valueRange = 0f..mp3.durationMs.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            "Time: ${Mp3Controller.formatTimeMs(currentMs)} / ${Mp3Controller.formatTimeMs(mp3.durationMs)}"
        )
    }
}
