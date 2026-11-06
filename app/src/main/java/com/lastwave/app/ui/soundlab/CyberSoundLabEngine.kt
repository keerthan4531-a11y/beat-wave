package com.lastwave.app.ui.soundlab

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

data class SoundLabState(
    val isActive: Boolean = false,
    val rainLevel: Float = 0.45f,
    val spaceDroneLevel: Float = 0.30f,
    val binauralAlphaLevel: Float = 0.25f,
    val vinylWarmthLevel: Float = 0.15f,
    val masterGain: Float = 0.70f,
)

/**
 * Procedural Cyber Soundscape & Binaural Synth Engine for BeatWave.
 * Synthesizes 44.1 kHz stereo PCM audio in real time so users can play
 * focus atmospheres standalone or layered gently beneath music playback.
 */
@Singleton
class CyberSoundLabEngine @Inject constructor() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _state = MutableStateFlow(SoundLabState())
    val state: StateFlow<SoundLabState> = _state.asStateFlow()

    private var synthJob: Job? = null

    fun toggleActive() {
        val next = !_state.value.isActive
        _state.update { it.copy(isActive = next) }
        if (next) startSynth() else stopSynth()
    }

    fun setActive(enabled: Boolean) {
        if (_state.value.isActive == enabled) return
        _state.update { it.copy(isActive = enabled) }
        if (enabled) startSynth() else stopSynth()
    }

    fun setRainLevel(value: Float) = _state.update { it.copy(rainLevel = value.coerceIn(0f, 1f)) }
    fun setSpaceDroneLevel(value: Float) = _state.update { it.copy(spaceDroneLevel = value.coerceIn(0f, 1f)) }
    fun setBinauralAlphaLevel(value: Float) = _state.update { it.copy(binauralAlphaLevel = value.coerceIn(0f, 1f)) }
    fun setVinylWarmthLevel(value: Float) = _state.update { it.copy(vinylWarmthLevel = value.coerceIn(0f, 1f)) }
    fun setMasterGain(value: Float) = _state.update { it.copy(masterGain = value.coerceIn(0f, 1f)) }

    fun applyPreset(rain: Float, drone: Float, binaural: Float, vinyl: Float) {
        _state.update {
            it.copy(
                rainLevel = rain.coerceIn(0f, 1f),
                spaceDroneLevel = drone.coerceIn(0f, 1f),
                binauralAlphaLevel = binaural.coerceIn(0f, 1f),
                vinylWarmthLevel = vinyl.coerceIn(0f, 1f),
                isActive = true,
            )
        }
        startSynth()
    }

    private fun startSynth() {
        if (synthJob?.isActive == true) return
        synthJob = scope.launch {
            val sampleRate = 44100
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
            ).coerceAtLeast(4096)

            val track = runCatching {
                AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build(),
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                            .build(),
                    )
                    .setBufferSizeInBytes(minBuf * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
            }.getOrNull() ?: return@launch

            val framesPerChunk = 1024
            val pcm = ShortArray(framesPerChunk * 2)

            var phaseDrone1 = 0.0
            var phaseDrone2 = 0.0
            var phaseDrone3 = 0.0
            var phaseBinL = 0.0
            var phaseBinR = 0.0
            var lfoPhase = 0.0

            // Pink/brown filtered rain state
            var rainLpL = 0f
            var rainLpR = 0f
            var vinylCrackleHold = 0f
            val rng = Random(4531)

            val twoPiOverSr = (2.0 * PI) / sampleRate.toDouble()

            try {
                track.play()
                while (isActive && _state.value.isActive) {
                    val cur = _state.value
                    val master = cur.masterGain * 0.42f

                    for (i in 0 until framesPerChunk) {
                        lfoPhase += 0.18 * twoPiOverSr
                        if (lfoPhase > 2.0 * PI) lfoPhase -= 2.0 * PI
                        val breath = (0.78 + 0.22 * sin(lfoPhase)).toFloat()

                        // 1. Cyber Rain (low-passed stereo pink wash)
                        val whiteL = rng.nextFloat() * 2f - 1f
                        val whiteR = rng.nextFloat() * 2f - 1f
                        rainLpL += 0.085f * (whiteL - rainLpL)
                        rainLpR += 0.085f * (whiteR - rainLpR)
                        val rainSampleL = rainLpL * cur.rainLevel * 0.85f
                        val rainSampleR = rainLpR * cur.rainLevel * 0.85f

                        // 2. 432 Hz Deep Space Harmonic Drone (108 Hz + 216 Hz + 324 Hz)
                        phaseDrone1 += 108.0 * twoPiOverSr
                        phaseDrone2 += 216.0 * twoPiOverSr
                        phaseDrone3 += 324.0 * twoPiOverSr
                        if (phaseDrone1 > 2.0 * PI) phaseDrone1 -= 2.0 * PI
                        if (phaseDrone2 > 2.0 * PI) phaseDrone2 -= 2.0 * PI
                        if (phaseDrone3 > 2.0 * PI) phaseDrone3 -= 2.0 * PI
                        val drone = (
                            0.52 * sin(phaseDrone1) +
                                0.32 * sin(phaseDrone2) +
                                0.16 * sin(phaseDrone3)
                            ).toFloat() * cur.spaceDroneLevel * 0.36f * breath

                        // 3. Binaural Alpha Focus (200 Hz Left, 210 Hz Right -> 10 Hz Alpha Wave)
                        phaseBinL += 200.0 * twoPiOverSr
                        phaseBinR += 210.0 * twoPiOverSr
                        if (phaseBinL > 2.0 * PI) phaseBinL -= 2.0 * PI
                        if (phaseBinR > 2.0 * PI) phaseBinR -= 2.0 * PI
                        val binL = sin(phaseBinL).toFloat() * cur.binauralAlphaLevel * 0.24f
                        val binR = sin(phaseBinR).toFloat() * cur.binauralAlphaLevel * 0.24f

                        // 4. Analog Vinyl Warmth Crackle
                        if (rng.nextFloat() < 0.0014f) {
                            vinylCrackleHold = (rng.nextFloat() * 2f - 1f) * 0.55f
                        } else {
                            vinylCrackleHold *= 0.86f
                        }
                        val vinyl = vinylCrackleHold * cur.vinylWarmthLevel

                        val outL = ((rainSampleL + drone + binL + vinyl) * master).coerceIn(-0.95f, 0.95f)
                        val outR = ((rainSampleR + drone + binR + vinyl) * master).coerceIn(-0.95f, 0.95f)

                        pcm[i * 2] = (outL * 32767f).toInt().toShort()
                        pcm[i * 2 + 1] = (outR * 32767f).toInt().toShort()
                    }
                    track.write(pcm, 0, pcm.size)
                }
            } catch (_: Throwable) {
            } finally {
                runCatching { track.stop() }
                runCatching { track.release() }
            }
        }
    }

    private fun stopSynth() {
        synthJob?.cancel()
        synthJob = null
    }
}
