package com.lastwave.app.ui.soundlab

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lastwave.app.playback.MusicPlayer
import com.lastwave.app.ui.common.adaptiveContentWidth
import com.lastwave.app.ui.theme.CyberDisplayFont
import com.lastwave.app.ui.theme.CyberMonoFont
import com.lastwave.app.ui.theme.CyberUiFont
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin

@HiltViewModel
class CyberSoundLabViewModel @Inject constructor(
    val engine: CyberSoundLabEngine,
    private val musicPlayer: MusicPlayer,
) : ViewModel() {
    val labState = engine.state
    val playerState = musicPlayer.state

    fun setPlaybackParameters(speed: Float, pitch: Float) {
        musicPlayer.setPlaybackParameters(speed, pitch)
    }

    fun resetPlaybackParameters() {
        musicPlayer.resetPlaybackParameters()
    }

    fun setSleepTimerMinutes(minutes: Int) {
        musicPlayer.setSleepTimerMinutes(minutes)
    }
}

private val CyberBg = Color(0xFF060914)
private val CyberCardBg = Color(0xFF0D1328)
private val NeonCyan = Color(0xFF00F2FE)
private val NeonViolet = Color(0xFFA855F7)
private val NeonBlue = Color(0xFF3B82F6)
private val NeonGreen = Color(0xFF10B981)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CyberSoundLabScreen(
    onBack: () -> Unit,
    viewModel: CyberSoundLabViewModel = hiltViewModel(),
) {
    val labState by viewModel.labState.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    val infinite = rememberInfiniteTransition(label = "soundlab_wave")
    val wavePhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wave_phase",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF050812), CyberBg, Color(0xFF090D1F)),
                ),
            ),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .adaptiveContentWidth(maxWidth = 840.dp)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CyberCardBg)
                        .border(1.dp, NeonCyan.copy(alpha = 0.45f), CircleShape)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "BEATWAVE // DSP MATRIX",
                        fontFamily = CyberMonoFont,
                        fontSize = 11.sp,
                        letterSpacing = 1.6.sp,
                        color = NeonCyan,
                    )
                    Text(
                        text = "CYBER SOUND LAB",
                        fontFamily = CyberDisplayFont,
                        fontWeight = FontWeight.Black,
                        fontSize = 21.sp,
                        letterSpacing = 1.2.sp,
                        color = Color.White,
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (labState.isActive) NeonGreen.copy(alpha = 0.18f) else CyberCardBg,
                    modifier = Modifier.border(
                        1.dp,
                        if (labState.isActive) NeonGreen else NeonCyan.copy(alpha = 0.3f),
                        RoundedCornerShape(50),
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { viewModel.engine.toggleActive() }
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (labState.isActive) NeonGreen else Color(0xFF64748B)),
                        )
                        Text(
                            text = if (labState.isActive) "SYNTH LIVE" else "STANDBY",
                            fontFamily = CyberMonoFont,
                            fontSize = 11.sp,
                            color = if (labState.isActive) NeonGreen else Color(0xFFCBD5E1),
                        )
                    }
                }
            }

            // 1. Live Oscilloscope & Synth Master Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CyberCardBg)
                    .border(
                        width = 1.4.dp,
                        brush = Brush.linearGradient(listOf(NeonCyan.copy(alpha = 0.7f), NeonViolet.copy(alpha = 0.7f))),
                        shape = RoundedCornerShape(24.dp),
                    )
                    .padding(18.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "BINAURAL & AMBIENT SYNTHESIZER",
                                fontFamily = CyberMonoFont,
                                fontSize = 11.sp,
                                letterSpacing = 1.3.sp,
                                color = NeonCyan,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "44.1 kHz Procedural Soundstage",
                                fontFamily = CyberUiFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        if (labState.isActive) listOf(NeonCyan, NeonBlue)
                                        else listOf(Color(0xFF1E293B), Color(0xFF0F172A)),
                                    ),
                                )
                                .border(1.5.dp, NeonCyan.copy(alpha = 0.7f), CircleShape)
                                .clickable { viewModel.engine.toggleActive() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = if (labState.isActive) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = if (labState.isActive) "Stop Synth" else "Start Synth",
                                tint = if (labState.isActive) Color(0xFF04101E) else NeonCyan,
                                modifier = Modifier.size(28.dp),
                            )
                        }
                    }

                    // Animated Cyber Oscilloscope Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF070B18))
                            .border(1.dp, NeonCyan.copy(alpha = 0.22f), RoundedCornerShape(16.dp)),
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val midY = h / 2f
                            val ampScale = if (labState.isActive) {
                                ((labState.rainLevel + labState.spaceDroneLevel + labState.binauralAlphaLevel) / 2.2f)
                                    .coerceIn(0.18f, 0.88f)
                            } else {
                                0.08f
                            }

                            // Grid lines
                            for (g in 1..3) {
                                val gy = h * g / 4f
                                drawLine(
                                    color = NeonCyan.copy(alpha = 0.08f),
                                    start = Offset(0f, gy),
                                    end = Offset(w, gy),
                                    strokeWidth = 1f,
                                )
                            }

                            val path1 = Path()
                            val path2 = Path()
                            val steps = 90
                            for (s in 0..steps) {
                                val t = s.toFloat() / steps.toFloat()
                                val x = t * w
                                val y1 = midY + sin(t * 4f * PI + wavePhase).toFloat() * (h * 0.36f * ampScale)
                                val y2 = midY + sin(t * 6.5f * PI - wavePhase * 1.3f).toFloat() * (h * 0.26f * ampScale)
                                if (s == 0) {
                                    path1.moveTo(x, y1)
                                    path2.moveTo(x, y2)
                                } else {
                                    path1.lineTo(x, y1)
                                    path2.lineTo(x, y2)
                                }
                            }
                            drawPath(
                                path = path2,
                                color = NeonViolet.copy(alpha = 0.65f),
                                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round),
                            )
                            drawPath(
                                path = path1,
                                brush = Brush.horizontalGradient(listOf(NeonCyan, NeonBlue, NeonViolet)),
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                            )
                        }
                    }

                    // Atmosphere Quick Presets
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SoundPresetChip(
                            label = "Cyber Rain",
                            icon = Icons.Rounded.WaterDrop,
                            onClick = { viewModel.engine.applyPreset(0.75f, 0.20f, 0.15f, 0.20f) },
                        )
                        SoundPresetChip(
                            label = "Deep Coding Alpha",
                            icon = Icons.Rounded.Psychology,
                            onClick = { viewModel.engine.applyPreset(0.30f, 0.35f, 0.65f, 0.05f) },
                        )
                        SoundPresetChip(
                            label = "432Hz Cosmos",
                            icon = Icons.Rounded.AutoAwesome,
                            onClick = { viewModel.engine.applyPreset(0.15f, 0.80f, 0.35f, 0.10f) },
                        )
                        SoundPresetChip(
                            label = "Lo-Fi Vinyl Lounge",
                            icon = Icons.Rounded.Album,
                            onClick = { viewModel.engine.applyPreset(0.45f, 0.25f, 0.10f, 0.65f) },
                        )
                    }

                    // Individual Stem Sliders
                    SoundChannelSlider(
                        icon = Icons.Rounded.WaterDrop,
                        title = "Neon Rain Wash",
                        value = labState.rainLevel,
                        accent = NeonCyan,
                        onValueChange = viewModel.engine::setRainLevel,
                    )
                    SoundChannelSlider(
                        icon = Icons.Rounded.Memory,
                        title = "432 Hz Harmonic Space Drone",
                        value = labState.spaceDroneLevel,
                        accent = NeonViolet,
                        onValueChange = viewModel.engine::setSpaceDroneLevel,
                    )
                    SoundChannelSlider(
                        icon = Icons.Rounded.Headphones,
                        title = "10 Hz Alpha Binaural Focus",
                        value = labState.binauralAlphaLevel,
                        accent = NeonBlue,
                        onValueChange = viewModel.engine::setBinauralAlphaLevel,
                    )
                    SoundChannelSlider(
                        icon = Icons.Rounded.Album,
                        title = "Analog Vinyl Texture",
                        value = labState.vinylWarmthLevel,
                        accent = Color(0xFFF472B6),
                        onValueChange = viewModel.engine::setVinylWarmthLevel,
                    )
                }
            }

            // 2. Real-Time Music Pitch, 432 Hz & Tempo Matrix
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CyberCardBg)
                    .border(
                        width = 1.2.dp,
                        color = NeonViolet.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(24.dp),
                    )
                    .padding(18.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "PLAYBACK HARMONIC TUNER",
                                fontFamily = CyberMonoFont,
                                fontSize = 11.sp,
                                letterSpacing = 1.3.sp,
                                color = NeonViolet,
                            )
                            Text(
                                text = "432 Hz, Slowed & Nightcore DSP",
                                fontFamily = CyberUiFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF17203D))
                                .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(50))
                                .clickable { viewModel.resetPlaybackParameters() }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.RestartAlt,
                                    contentDescription = "Reset",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(16.dp),
                                )
                                Text(
                                    text = "1.0x RESET",
                                    fontFamily = CyberMonoFont,
                                    fontSize = 11.sp,
                                    color = NeonCyan,
                                )
                            }
                        }
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val s = playerState.speed
                        val p = playerState.pitch
                        DspModeChip(
                            label = "Studio 1.0x",
                            selected = abs(s - 1.0f) < 0.01f && abs(p - 1.0f) < 0.01f,
                            onClick = { viewModel.setPlaybackParameters(1.0f, 1.0f) },
                        )
                        DspModeChip(
                            label = "432 Hz Harmonic",
                            selected = abs(s - 1.0f) < 0.01f && abs(p - 0.982f) < 0.01f,
                            onClick = { viewModel.setPlaybackParameters(1.0f, 0.982f) },
                        )
                        DspModeChip(
                            label = "Slowed Aesthetic",
                            selected = abs(s - 0.86f) < 0.02f && abs(p - 0.88f) < 0.02f,
                            onClick = { viewModel.setPlaybackParameters(0.86f, 0.88f) },
                        )
                        DspModeChip(
                            label = "Nightcore Boost",
                            selected = abs(s - 1.18f) < 0.02f && abs(p - 1.16f) < 0.02f,
                            onClick = { viewModel.setPlaybackParameters(1.18f, 1.16f) },
                        )
                        DspModeChip(
                            label = "Deep Vapor 0.78x",
                            selected = abs(s - 0.78f) < 0.02f && abs(p - 0.80f) < 0.02f,
                            onClick = { viewModel.setPlaybackParameters(0.78f, 0.80f) },
                        )
                    }

                    // Fine Speed & Pitch Controls
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Rounded.Speed, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                Text("Tempo / Speed", fontFamily = CyberUiFont, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
                            }
                            Text(
                                text = "${((playerState.speed * 100).roundToInt() / 100f)}x",
                                fontFamily = CyberMonoFont,
                                fontSize = 12.sp,
                                color = NeonCyan,
                            )
                        }
                        Slider(
                            value = playerState.speed.coerceIn(0.65f, 1.45f),
                            onValueChange = { viewModel.setPlaybackParameters(it, playerState.pitch) },
                            valueRange = 0.65f..1.45f,
                            colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan),
                        )
                    }

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Rounded.Tune, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(16.dp))
                                Text("Harmonic Pitch", fontFamily = CyberUiFont, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
                            }
                            Text(
                                text = "${((playerState.pitch * 1000).roundToInt() / 1000f)}x",
                                fontFamily = CyberMonoFont,
                                fontSize = 12.sp,
                                color = NeonViolet,
                            )
                        }
                        Slider(
                            value = playerState.pitch.coerceIn(0.70f, 1.35f),
                            onValueChange = { viewModel.setPlaybackParameters(playerState.speed, it) },
                            valueRange = 0.70f..1.35f,
                            colors = SliderDefaults.colors(thumbColor = NeonViolet, activeTrackColor = NeonViolet),
                        )
                    }
                }
            }

            // 3. Smart Sleep Fade Matrix
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CyberCardBg)
                    .border(1.dp, NeonBlue.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                    .padding(18.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(NeonBlue.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Rounded.Bedtime, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text(
                                    text = "SMART SLEEP FADE TIMER",
                                    fontFamily = CyberMonoFont,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.2.sp,
                                    color = NeonBlue,
                                )
                                val remMin = playerState.sleepTimerRemainingMs?.let { ((it + 59_999L) / 60_000L).coerceAtLeast(1L) }
                                Text(
                                    text = if (remMin != null) "Active • Auto-stop in ${remMin}m" else "Schedule automatic playback shutdown",
                                    fontFamily = CyberUiFont,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = Color.White,
                                )
                            }
                        }
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        listOf(0 to "Off", 15 to "15 Min", 30 to "30 Min", 45 to "45 Min", 60 to "60 Min").forEach { (mins, label) ->
                            val isOffSelected = mins == 0 && playerState.sleepTimerRemainingMs == null
                            DspModeChip(
                                label = label,
                                selected = isOffSelected,
                                onClick = { viewModel.setSleepTimerMinutes(mins) },
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SoundPresetChip(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color(0xFF141D3B),
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .border(1.dp, NeonCyan.copy(alpha = 0.35f), RoundedCornerShape(50))
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(15.dp))
            Text(
                text = label,
                fontFamily = CyberUiFont,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun DspModeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) NeonCyan.copy(alpha = 0.22f) else Color(0xFF141D3B),
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .border(
                width = 1.dp,
                color = if (selected) NeonCyan else Color(0xFF2B3B6B),
                shape = RoundedCornerShape(50),
            )
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = if (selected) Icons.Rounded.Bolt else Icons.Rounded.GraphicEq,
                contentDescription = null,
                tint = if (selected) NeonCyan else Color(0xFF94A3B8),
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = label,
                fontFamily = CyberUiFont,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (selected) NeonCyan else Color(0xFFE2E8F0),
            )
        }
    }
}

@Composable
private fun SoundChannelSlider(
    icon: ImageVector,
    title: String,
    value: Float,
    accent: Color,
    onValueChange: (Float) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = icon, contentDescription = null, tint = accent, modifier = Modifier.size(17.dp))
                Text(
                    text = title,
                    fontFamily = CyberUiFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFFE2E8F0),
                )
            }
            Text(
                text = "${(value * 100).roundToInt()}%",
                fontFamily = CyberMonoFont,
                fontSize = 12.sp,
                color = accent,
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = accent,
                activeTrackColor = accent,
                inactiveTrackColor = accent.copy(alpha = 0.2f),
            ),
        )
    }
}
