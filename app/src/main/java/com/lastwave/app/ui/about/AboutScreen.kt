package com.lastwave.app.ui.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lastwave.app.R
import com.lastwave.app.ui.common.AnimatedGifBanner
import com.lastwave.app.ui.common.adaptiveContentWidth
import com.lastwave.app.ui.theme.CyberDisplayFont
import com.lastwave.app.ui.theme.CyberMonoFont
import com.lastwave.app.ui.theme.CyberUiFont
import kotlinx.coroutines.delay

const val CREATOR_INSTAGRAM_URL = "https://www.instagram.com/dark.shadow_4531?stkn=MTZua29kbXpoOGhnMA=="
const val BEATWAVE_GITHUB_URL = "https://github.com/keerthan4531-a11y/beat-wave"

private val DeepCyberBg = Color(0xFF050812)
private val CyberSurface = Color(0xFF0C1226)
private val CyberSurfaceHigh = Color(0xFF121B38)
private val CyberCyan = Color(0xFF00F2FE)
private val CyberBlue = Color(0xFF3B82F6)
private val CyberViolet = Color(0xFFA855F7)
private val CyberPink = Color(0xFFEC4899)
private val CyberEmerald = Color(0xFF10B981)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit,
    onOpenSoundLab: () -> Unit = {},
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val versionName = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "5.0.0"
        }.getOrDefault("5.0.0")
    }

    // High-energy staggered cyber entrance choreography
    var showTopBar by remember { mutableStateOf(false) }
    var showHero by remember { mutableStateOf(false) }
    var showFeatures by remember { mutableStateOf(false) }
    var showCreator by remember { mutableStateOf(false) }
    var showInstagram by remember { mutableStateOf(false) }
    var showSpecs by remember { mutableStateOf(false) }
    var showGifFooter by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showTopBar = true
        delay(80)
        showHero = true
        delay(110)
        showFeatures = true
        delay(110)
        showCreator = true
        delay(110)
        showInstagram = true
        delay(100)
        showSpecs = true
        delay(100)
        showGifFooter = true
    }

    val infinite = rememberInfiniteTransition(label = "about_cyber_fx")
    val ringRotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
        ),
        label = "ring_rotation",
    )
    val pulseScale by infinite.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse_scale",
    )
    val scanlineOffset by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5500, easing = LinearEasing),
        ),
        label = "scanline",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepCyberBg, Color(0xFF080D1E), Color(0xFF050710)),
                ),
            ),
        contentAlignment = Alignment.TopCenter,
    ) {
        // Subtle Animated Cyber Grid & Scanline HUD Backdrop
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 44.dp.toPx()
            var x = 0f
            while (x < size.width) {
                drawLine(
                    color = CyberCyan.copy(alpha = 0.035f),
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1f,
                )
                x += step
            }
            var y = 0f
            while (y < size.height) {
                drawLine(
                    color = CyberViolet.copy(alpha = 0.03f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f,
                )
                y += step
            }
            val scanY = size.height * scanlineOffset
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, CyberCyan.copy(alpha = 0.20f), Color.Transparent),
                ),
                start = Offset(0f, scanY),
                end = Offset(size.width, scanY),
                strokeWidth = 2.dp.toPx(),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .adaptiveContentWidth(maxWidth = 820.dp)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            // Top Header
            AnimatedVisibility(
                visible = showTopBar,
                enter = fadeIn(tween(300)) + slideInVertically(tween(320)) { -it / 2 },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(CyberSurfaceHigh)
                            .border(1.dp, CyberCyan.copy(alpha = 0.5f), CircleShape)
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
                    Column {
                        Text(
                            text = "SYSTEM ARCHITECTURE // TELEMETRY",
                            fontFamily = CyberMonoFont,
                            fontSize = 10.sp,
                            letterSpacing = 1.6.sp,
                            color = CyberCyan,
                        )
                        Text(
                            text = "ABOUT BEATWAVE",
                            fontFamily = CyberDisplayFont,
                            fontWeight = FontWeight.Black,
                            fontSize = 21.sp,
                            letterSpacing = 1.4.sp,
                            color = Color.White,
                        )
                    }
                }
            }

            // 1. App Identity Cyber Hero Card
            AnimatedVisibility(
                visible = showHero,
                enter = fadeIn(tween(420)) + scaleIn(
                    initialScale = 0.88f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                ),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(CyberSurface)
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                listOf(CyberCyan, CyberBlue, CyberViolet),
                            ),
                            shape = RoundedCornerShape(26.dp),
                        )
                        .padding(vertical = 26.dp, horizontal = 20.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // Rotating Conic Neon Ring + Transparent AI BeatWave Logo
                        Box(
                            modifier = Modifier.size(104.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .rotate(ringRotation)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.sweepGradient(
                                            listOf(CyberCyan, CyberBlue, CyberViolet, CyberPink, CyberCyan),
                                        ),
                                    ),
                            )
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF070B19)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_beatwave_logo),
                                    contentDescription = "BeatWave Logo",
                                    modifier = Modifier
                                        .size(72.dp)
                                        .scale(pulseScale),
                                    contentScale = ContentScale.Fit,
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "BEATWAVE",
                            fontFamily = CyberDisplayFont,
                            fontWeight = FontWeight.Black,
                            fontSize = 28.sp,
                            letterSpacing = 3.sp,
                            color = Color.White,
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = "// NEXT-GEN LOSSLESS CYBER AUDIO MATRIX",
                            fontFamily = CyberMonoFont,
                            fontSize = 11.sp,
                            letterSpacing = 1.4.sp,
                            color = CyberCyan,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(Modifier.height(16.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            CyberTagPill("VER: $versionName", CyberCyan)
                            CyberTagPill("LIQUID GLASS", CyberBlue)
                            CyberTagPill("32-BIT DSP", CyberViolet)
                            CyberTagPill("432HZ LAB", CyberEmerald)
                        }
                    }
                }
            }

            // 2. Highlighted Extra Features Section
            AnimatedVisibility(
                visible = showFeatures,
                enter = fadeIn(tween(420)) + slideInVertically(tween(420)) { it / 3 },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(CyberSurface)
                        .border(
                            width = 1.3.dp,
                            brush = Brush.linearGradient(
                                listOf(CyberViolet.copy(alpha = 0.8f), CyberCyan.copy(alpha = 0.8f)),
                            ),
                            shape = RoundedCornerShape(26.dp),
                        )
                        .padding(20.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Bolt,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(20.dp),
                                )
                                Text(
                                    text = "HIGHLIGHTED CYBER MODULES",
                                    fontFamily = CyberMonoFont,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.4.sp,
                                    color = CyberCyan,
                                )
                            }
                            CyberTagPill("EXCLUSIVE", CyberPink)
                        }

                        HighlightedFeatureItem(
                            icon = Icons.Rounded.GraphicEq,
                            accent = CyberCyan,
                            title = "Cyber Sound Lab & Binaural Synth",
                            description = "Real-time 44.1 kHz procedural Neon Rain, 432 Hz Space Drone, 10 Hz Alpha coding focus & Vinyl texture engine.",
                        )
                        HighlightedFeatureItem(
                            icon = Icons.Rounded.Tune,
                            accent = CyberViolet,
                            title = "432 Hz Harmonic, Slowed & Nightcore DSP",
                            description = "Instant studio pitch & tempo matrix with one-tap 432 Hz tuning, Slowed Aesthetic, Nightcore & Smart Sleep Fade.",
                        )
                        HighlightedFeatureItem(
                            icon = Icons.Rounded.Person,
                            accent = CyberBlue,
                            title = "Cyber Profile Hub & Acoustic Telemetry",
                            description = "Personalized listener callsign, dynamic Acoustic Rank aura, offline vault metrics & unified customization hub.",
                        )
                        HighlightedFeatureItem(
                            icon = Icons.Rounded.Headphones,
                            accent = CyberEmerald,
                            title = "Bit-Perfect USB DAC, 15-Band PEQ & Karaoke",
                            description = "Hardware mixer bypass, AutoEQ headphone curves, DJ Automix crossfader & millisecond-synced lyrics.",
                        )

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = CyberCyan.copy(alpha = 0.14f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, CyberCyan.copy(alpha = 0.65f), RoundedCornerShape(16.dp))
                                .clickable(onClick = onOpenSoundLab),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Equalizer,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(19.dp),
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "LAUNCH CYBER SOUND LAB",
                                    fontFamily = CyberDisplayFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    letterSpacing = 1.4.sp,
                                    color = CyberCyan,
                                )
                                Spacer(Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(17.dp),
                                )
                            }
                        }
                    }
                }
            }

            // 3. Creator / System Architect Card ("mokka coding" + Circular DP)
            AnimatedVisibility(
                visible = showCreator,
                enter = fadeIn(tween(450)) + scaleIn(initialScale = 0.92f, animationSpec = tween(450)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(CyberSurface)
                        .border(
                            width = 1.4.dp,
                            brush = Brush.linearGradient(
                                listOf(CyberEmerald.copy(alpha = 0.7f), CyberCyan, CyberViolet),
                            ),
                            shape = RoundedCornerShape(26.dp),
                        )
                        .padding(22.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "SYSTEM ARCHITECT",
                                fontFamily = CyberMonoFont,
                                fontSize = 12.sp,
                                letterSpacing = 1.5.sp,
                                color = CyberEmerald,
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .scale(pulseScale)
                                        .clip(CircleShape)
                                        .background(CyberEmerald),
                                )
                                Text(
                                    text = "ONLINE",
                                    fontFamily = CyberMonoFont,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.2.sp,
                                    color = CyberEmerald,
                                )
                            }
                        }

                        Spacer(Modifier.height(18.dp))

                        // Circular DP with rotating neon ring
                        Box(
                            modifier = Modifier.size(116.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .rotate(ringRotation)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.sweepGradient(
                                            listOf(CyberEmerald, CyberCyan, CyberViolet, CyberEmerald),
                                        ),
                                    ),
                            )
                            Image(
                                painter = painterResource(id = R.drawable.creator_dp),
                                contentDescription = "mokka coding avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(108.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color(0xFF060A18), CircleShape),
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = "mokka coding",
                                fontFamily = CyberDisplayFont,
                                fontWeight = FontWeight.Black,
                                fontSize = 23.sp,
                                letterSpacing = 1.2.sp,
                                color = Color.White,
                            )
                            Icon(
                                imageVector = Icons.Rounded.Verified,
                                contentDescription = "Verified Creator",
                                tint = CyberCyan,
                                modifier = Modifier.size(20.dp),
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberCyan.copy(alpha = 0.12f))
                                .border(1.dp, CyberCyan.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = "[ LEAD CREATOR & CORE DEVELOPER ]",
                                fontFamily = CyberMonoFont,
                                fontSize = 11.sp,
                                letterSpacing = 1.2.sp,
                                color = CyberCyan,
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "Creator of BeatWave • Engineering high-performance lossless audio pipelines with futuristic cyber-glass aesthetics.",
                            fontFamily = CyberUiFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            lineHeight = 21.sp,
                            color = Color(0xFFB8C7E0),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            // 4. Instagram Card (Showing Insta DP + Custom Vector Instagram Logo)
            AnimatedVisibility(
                visible = showInstagram,
                enter = fadeIn(tween(450)) + slideInVertically(tween(450)) { it / 2 },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(CyberSurface)
                        .border(
                            width = 1.4.dp,
                            brush = Brush.linearGradient(
                                listOf(CyberPink, CyberViolet, Color(0xFFF59E0B)),
                            ),
                            shape = RoundedCornerShape(24.dp),
                        )
                        .clickable { openExternalUrl(context, CREATOR_INSTAGRAM_URL) }
                        .padding(18.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "COMMUNICATION // INSTAGRAM",
                                fontFamily = CyberMonoFont,
                                fontSize = 11.sp,
                                letterSpacing = 1.4.sp,
                                color = CyberPink,
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.ic_instagram_vector),
                                contentDescription = "Instagram Logo",
                                tint = CyberPink,
                                modifier = Modifier.size(20.dp),
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Circular Instagram DP + small vector badge
                            Box(
                                modifier = Modifier.size(64.dp),
                                contentAlignment = Alignment.BottomEnd,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(CyberPink, CyberViolet, Color(0xFFF59E0B)),
                                            ),
                                        )
                                        .padding(2.5.dp),
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.insta_dp),
                                        contentDescription = "@dark.shadow_4531 Instagram DP",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape),
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(CyberPink)
                                        .border(1.5.dp, CyberSurface, CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_instagram_vector),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp),
                                    )
                                }
                            }

                            Spacer(Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "@dark.shadow_4531",
                                    fontFamily = CyberUiFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.White,
                                )
                                Text(
                                    text = "Official Instagram ID • Connect & DM",
                                    fontFamily = CyberUiFont,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = Color(0xFFA4B8D8),
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(CyberPink.copy(alpha = 0.18f))
                                    .border(1.dp, CyberPink.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 14.dp, vertical = 9.dp),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                ) {
                                    Text(
                                        text = "FOLLOW",
                                        fontFamily = CyberMonoFont,
                                        fontSize = 12.sp,
                                        color = CyberPink,
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                        contentDescription = null,
                                        tint = CyberPink,
                                        modifier = Modifier.size(14.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Cyber Repository & Engine Architecture Strip
            AnimatedVisibility(
                visible = showSpecs,
                enter = fadeIn(tween(450)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(CyberSurface)
                        .border(1.dp, CyberBlue.copy(alpha = 0.35f), RoundedCornerShape(22.dp))
                        .clickable { openExternalUrl(context, BEATWAVE_GITHUB_URL) }
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyberBlue.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Code,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "BeatWave Open Architecture",
                                fontFamily = CyberUiFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White,
                            )
                            Text(
                                text = "Kotlin • Jetpack Compose • Media3 ExoPlayer • Oboe C++ DSP",
                                fontFamily = CyberMonoFont,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8),
                            )
                        }
                        Icon(
                            imageVector = Icons.Rounded.OpenInNew,
                            contentDescription = "Open Repository",
                            tint = CyberCyan,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }

            // 6. LAST ITEM AT BOTTOM: Animated LMEB.gif Cyber Showcase
            AnimatedVisibility(
                visible = showGifFooter,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 2 },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(CyberSurface)
                        .border(
                            width = 1.4.dp,
                            brush = Brush.linearGradient(
                                listOf(CyberCyan.copy(alpha = 0.7f), CyberViolet.copy(alpha = 0.7f)),
                            ),
                            shape = RoundedCornerShape(26.dp),
                        )
                        .padding(14.dp),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Memory,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(15.dp),
                                )
                                Text(
                                    text = "SIGNATURE VISUAL // mokka coding",
                                    fontFamily = CyberMonoFont,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.3.sp,
                                    color = CyberCyan,
                                )
                            }
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = CyberViolet,
                                modifier = Modifier.size(15.dp),
                            )
                        }

                        AnimatedGifBanner(
                            rawResId = R.raw.lmeb,
                            contentDescription = "BeatWave Signature Cyber Animation",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(196.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .border(1.dp, CyberCyan.copy(alpha = 0.25f), RoundedCornerShape(18.dp)),
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun CyberTagPill(
    text: String,
    accent: Color,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(9.dp))
            .background(accent.copy(alpha = 0.13f))
            .border(1.dp, accent.copy(alpha = 0.50f), RoundedCornerShape(9.dp))
            .padding(horizontal = 11.dp, vertical = 5.dp),
    ) {
        Text(
            text = text,
            fontFamily = CyberMonoFont,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            color = accent,
        )
    }
}

@Composable
private fun HighlightedFeatureItem(
    icon: ImageVector,
    accent: Color,
    title: String,
    description: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CyberSurfaceHigh.copy(alpha = 0.72f))
            .border(1.dp, accent.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(13.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(accent.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = CyberUiFont,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = description,
                fontFamily = CyberUiFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = Color(0xFFA4B8D8),
            )
        }
    }
}

fun openExternalUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(intent) }
}
