package com.lastwave.app.ui.onboarding

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lastwave.app.R
import com.lastwave.app.ui.about.CREATOR_INSTAGRAM_URL
import com.lastwave.app.ui.about.openExternalUrl
import com.lastwave.app.ui.common.AnimatedGifBanner
import com.lastwave.app.ui.common.adaptiveContentWidth
import com.lastwave.app.ui.theme.CyberDisplayFont
import com.lastwave.app.ui.theme.CyberMonoFont
import com.lastwave.app.ui.theme.CyberUiFont

private const val PREFS_NAME = "beatwave_onboarding"
private const val KEY_WELCOME_SEEN = "has_completed_cyber_welcome_v1"

object BeatWaveOnboardingStore {
    fun shouldShowWelcome(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return !prefs.getBoolean(KEY_WELCOME_SEEN, false)
    }

    fun markWelcomeCompleted(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_WELCOME_SEEN, true)
            .apply()
    }
}

private val WelcomeCyan = Color(0xFF00F2FE)
private val WelcomeBlue = Color(0xFF3B82F6)
private val WelcomeViolet = Color(0xFFA855F7)
private val WelcomePink = Color(0xFFEC4899)
private val WelcomeEmerald = Color(0xFF10B981)

@Composable
fun CyberWelcomeDialog(
    onComplete: () -> Unit,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    val infinite = rememberInfiniteTransition(label = "welcome_cyber")
    val ringRotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(7500, easing = LinearEasing)),
        label = "welcome_ring",
    )
    val logoPulse by infinite.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "welcome_pulse",
    )

    Dialog(
        onDismissRequest = {
            BeatWaveOnboardingStore.markWelcomeCompleted(context)
            onComplete()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF2040712))
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(380)) + scaleIn(initialScale = 0.90f, animationSpec = tween(380)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .adaptiveContentWidth(maxWidth = 540.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF0B1126), Color(0xFF080C1C)),
                            ),
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                listOf(WelcomeCyan, WelcomeBlue, WelcomeViolet, WelcomePink),
                            ),
                            shape = RoundedCornerShape(28.dp),
                        ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        // Top System Activation Tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(WelcomeCyan.copy(alpha = 0.14f))
                                .border(1.dp, WelcomeCyan.copy(alpha = 0.55f), RoundedCornerShape(50))
                                .padding(horizontal = 14.dp, vertical = 5.dp),
                        ) {
                            Text(
                                text = "INITIAL SYSTEM ACTIVATION // v5.0",
                                fontFamily = CyberMonoFont,
                                fontSize = 10.sp,
                                letterSpacing = 1.4.sp,
                                color = WelcomeCyan,
                            )
                        }

                        // Rotating Neon Ring + Transparent AI BeatWave Logo
                        Box(
                            modifier = Modifier.size(86.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .rotate(ringRotation)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.sweepGradient(
                                            listOf(WelcomeCyan, WelcomeBlue, WelcomeViolet, WelcomePink, WelcomeCyan),
                                        ),
                                    ),
                            )
                            Box(
                                modifier = Modifier
                                    .size(79.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF060A18)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_beatwave_logo),
                                    contentDescription = "BeatWave Logo",
                                    modifier = Modifier
                                        .size(58.dp)
                                        .scale(logoPulse),
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "WELCOME TO BEATWAVE",
                                fontFamily = CyberDisplayFont,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                letterSpacing = 1.6.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Lossless Streaming • Liquid Glass • 432Hz Cyber Sound Lab",
                                fontFamily = CyberUiFont,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Color(0xFFA4B8D8),
                                textAlign = TextAlign.Center,
                            )
                        }

                        // Highlighted Cyber Modules Summary
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFF101833))
                                .border(1.dp, WelcomeCyan.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            WelcomeFeatureRow(
                                icon = Icons.Rounded.GraphicEq,
                                accent = WelcomeCyan,
                                title = "Cyber Sound Lab & Binaural Alpha Synth",
                            )
                            WelcomeFeatureRow(
                                icon = Icons.Rounded.Tune,
                                accent = WelcomeViolet,
                                title = "432 Hz Harmonic, Slowed & Nightcore Tuner",
                            )
                            WelcomeFeatureRow(
                                icon = Icons.Rounded.Headphones,
                                accent = WelcomeEmerald,
                                title = "Bit-Perfect USB DAC, 15-Band PEQ & Synced Lyrics",
                            )
                        }

                        // Creator Card ("mokka coding" + Circular DP)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFF101833))
                                .border(1.dp, WelcomeEmerald.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                                .padding(13.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(58.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.sweepGradient(
                                                listOf(WelcomeEmerald, WelcomeCyan, WelcomeViolet, WelcomeEmerald),
                                            ),
                                        )
                                        .padding(2.dp),
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.creator_dp),
                                        contentDescription = "mokka coding",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape),
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "SYSTEM ARCHITECT",
                                        fontFamily = CyberMonoFont,
                                        fontSize = 10.sp,
                                        letterSpacing = 1.2.sp,
                                        color = WelcomeEmerald,
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    ) {
                                        Text(
                                            text = "mokka coding",
                                            fontFamily = CyberDisplayFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp,
                                            color = Color.White,
                                        )
                                        Icon(
                                            imageVector = Icons.Rounded.Verified,
                                            contentDescription = null,
                                            tint = WelcomeCyan,
                                            modifier = Modifier.size(16.dp),
                                        )
                                    }
                                    Text(
                                        text = "Lead Creator & Core Developer",
                                        fontFamily = CyberUiFont,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8),
                                    )
                                }
                            }
                        }

                        // Instagram Card (Insta DP + Vector Instagram Logo)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFF101833))
                                .border(1.dp, WelcomePink.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                                .clickable { openExternalUrl(context, CREATOR_INSTAGRAM_URL) }
                                .padding(13.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier.size(52.dp),
                                    contentAlignment = Alignment.BottomEnd,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(WelcomePink, WelcomeViolet, Color(0xFFF59E0B)),
                                                ),
                                            )
                                            .padding(2.dp),
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.insta_dp),
                                            contentDescription = "@dark.shadow_4531",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape),
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(19.dp)
                                            .clip(CircleShape)
                                            .background(WelcomePink),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_instagram_vector),
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(11.dp),
                                        )
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "@dark.shadow_4531",
                                        fontFamily = CyberUiFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.White,
                                    )
                                    Text(
                                        text = "Official Instagram • Tap to Follow",
                                        fontFamily = CyberMonoFont,
                                        fontSize = 10.sp,
                                        color = WelcomePink,
                                    )
                                }
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_instagram_vector),
                                    contentDescription = "Instagram",
                                    tint = WelcomePink,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }

                        // Signature Animated LMEB.gif Preview
                        AnimatedGifBanner(
                            rawResId = R.raw.lmeb,
                            contentDescription = "BeatWave Cyber Visual",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(132.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, WelcomeCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                        )

                        // Primary Launch CTA Button
                        Button(
                            onClick = {
                                BeatWaveOnboardingStore.markWelcomeCompleted(context)
                                onComplete()
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WelcomeCyan,
                                contentColor = Color(0xFF03121E),
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Bolt,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "INITIALIZE BEATWAVE",
                                fontFamily = CyberDisplayFont,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                letterSpacing = 1.4.sp,
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeFeatureRow(
    icon: ImageVector,
    accent: Color,
    title: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(accent.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            text = title,
            fontFamily = CyberUiFont,
            fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp,
            color = Color.White,
        )
    }
}
