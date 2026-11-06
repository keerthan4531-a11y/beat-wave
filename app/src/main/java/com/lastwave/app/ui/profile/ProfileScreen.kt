package com.lastwave.app.ui.profile

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.lastwave.app.R
import com.lastwave.app.data.local.db.DownloadedTrackDao
import com.lastwave.app.data.local.db.SavedPlaylistDao
import com.lastwave.app.data.local.db.SongPlayStatsDao
import com.lastwave.app.data.local.db.SongPlayStatsEntity
import com.lastwave.app.data.ytmusic.YtMusicAuthManager
import com.lastwave.app.playback.MusicPlayer
import com.lastwave.app.ui.common.adaptiveContentWidth
import com.lastwave.app.ui.soundlab.CyberSoundLabEngine
import com.lastwave.app.ui.theme.CyberDisplayFont
import com.lastwave.app.ui.theme.CyberMonoFont
import com.lastwave.app.ui.theme.CyberUiFont
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val displayName: String = "BeatWave Listener",
    val bioTagline: String = "Cyber Audio Explorer • High-Res Vibes",
    val selectedVibe: String = "Lossless Cyber",
    val avatarUrl: String? = null,
    val isYtConnected: Boolean = false,
    val ytHandle: String? = null,
    val totalPlays: Int = 0,
    val uniqueSongs: Int = 0,
    val uniqueArtists: Int = 0,
    val totalListenMs: Long = 0L,
    val downloadedCount: Int = 0,
    val downloadedBytes: Long = 0L,
    val savedPlaylistsCount: Int = 0,
    val topTracks: List<SongPlayStatsEntity> = emptyList(),
    val isSoundLabActive: Boolean = false,
    val activeSpeed: Float = 1f,
    val activePitch: Float = 1f,
) {
    val acousticRank: String
        get() = when {
            totalPlays >= 250 -> "LOSSLESS OVERLORD"
            totalPlays >= 75 -> "SYNTH ARCHITECT"
            totalPlays >= 15 -> "PULSE NAVIGATOR"
            else -> "SIGNAL INITIATE"
        }
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext context: Context,
    songPlayStatsDao: SongPlayStatsDao,
    downloadedTrackDao: DownloadedTrackDao,
    private val savedPlaylistDao: SavedPlaylistDao,
    ytAuthManager: YtMusicAuthManager,
    soundLabEngine: CyberSoundLabEngine,
    musicPlayer: MusicPlayer,
) : ViewModel() {
    private val prefs = context.getSharedPreferences("beatwave_profile_prefs", Context.MODE_PRIVATE)

    private val customName = MutableStateFlow(prefs.getString("custom_name", "")?.trim().orEmpty())
    private val customBio = MutableStateFlow(
        prefs.getString("custom_bio", "Cyber Audio Explorer • High-Res Vibes")?.trim()
            .takeUnless { it.isNullOrBlank() } ?: "Cyber Audio Explorer • High-Res Vibes",
    )
    private val selectedVibe = MutableStateFlow(
        prefs.getString("selected_vibe", "Lossless Cyber") ?: "Lossless Cyber",
    )
    private val playlistsCount = MutableStateFlow(0)

    init {
        refreshPlaylistCount()
    }

    fun refreshPlaylistCount() {
        viewModelScope.launch {
            playlistsCount.value = runCatching { savedPlaylistDao.count() }.getOrDefault(0)
        }
    }

    val uiState: StateFlow<ProfileUiState> = combine(
        songPlayStatsDao.observeTopPlayed(2000),
        downloadedTrackDao.count(),
        downloadedTrackDao.totalBytes(),
        ytAuthManager.connection,
        combine(customName, customBio, selectedVibe, playlistsCount) { n, b, v, p ->
             ProfileCustomPrefs(n, b, v, p)
        },
    ) { playStats, dlCount, dlBytes, ytConn, custom ->
        val resolvedName = when {
            custom.name.isNotBlank() -> custom.name
            ytConn.accountName.isNotBlank() -> ytConn.accountName
            else -> "BeatWave Listener"
        }
        val totalPlays = playStats.sumOf { it.playCount }
        val totalMs = playStats.sumOf { it.totalPlayTimeMs }
        val artistsCount = playStats.mapNotNull { it.artist.trim().lowercase().takeIf { a -> a.isNotEmpty() } }
            .distinct()
            .size

        ProfileUiState(
            displayName = resolvedName,
            bioTagline = custom.bio,
            selectedVibe = custom.vibe,
            avatarUrl = ytConn.photoUrl,
            isYtConnected = ytConn.isConnected,
            ytHandle = ytConn.channelHandle,
            totalPlays = totalPlays,
            uniqueSongs = playStats.size,
            uniqueArtists = artistsCount,
            totalListenMs = totalMs,
            downloadedCount = dlCount,
            downloadedBytes = dlBytes ?: 0L,
            savedPlaylistsCount = custom.playlists,
            topTracks = playStats.take(3),
            isSoundLabActive = soundLabEngine.state.value.isActive,
            activeSpeed = musicPlayer.state.value.speed,
            activePitch = musicPlayer.state.value.pitch,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState(),
    )

    fun updateProfileIdentity(name: String, bio: String) {
        val cleanName = name.trim()
        val cleanBio = bio.trim().ifBlank { "Cyber Audio Explorer • High-Res Vibes" }
        prefs.edit()
            .putString("custom_name", cleanName)
            .putString("custom_bio", cleanBio)
            .apply()
        customName.value = cleanName
        customBio.value = cleanBio
    }

    fun selectVibe(vibe: String) {
        prefs.edit().putString("selected_vibe", vibe).apply()
        selectedVibe.value = vibe
    }

    private data class ProfileCustomPrefs(
        val name: String,
        val bio: String,
        val vibe: String,
        val playlists: Int,
    )
}

private val ProfileBg = Color(0xFF050812)
private val ProfileCard = Color(0xFF0C1226)
private val ProfileCardHigh = Color(0xFF131D3B)
private val AccentCyan = Color(0xFF00F2FE)
private val AccentBlue = Color(0xFF3B82F6)
private val AccentViolet = Color(0xFFA855F7)
private val AccentPink = Color(0xFFEC4899)
private val AccentEmerald = Color(0xFF10B981)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenSoundLab: () -> Unit,
    onOpenDownloads: () -> Unit,
    onOpenYouTubeLogin: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var showEditDialog by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.refreshPlaylistCount()
        visible = true
    }

    val infinite = rememberInfiniteTransition(label = "profile_glow")
    val ringRotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "ring_rot",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(ProfileBg, Color(0xFF080D1F), Color(0xFF050710)),
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
                        .background(ProfileCardHigh)
                        .border(1.dp, AccentCyan.copy(alpha = 0.45f), CircleShape)
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
                        text = "USER COMMAND HUB",
                        fontFamily = CyberMonoFont,
                        fontSize = 10.sp,
                        letterSpacing = 1.6.sp,
                        color = AccentCyan,
                    )
                    Text(
                        text = "PROFILE",
                        fontFamily = CyberDisplayFont,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        letterSpacing = 1.5.sp,
                        color = Color.White,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ProfileCardHigh)
                        .border(1.dp, AccentViolet.copy(alpha = 0.5f), CircleShape)
                        .clickable { showEditDialog = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit Profile",
                        tint = AccentCyan,
                        modifier = Modifier.size(19.dp),
                    )
                }
            }

            // 1. Cyber Listener Identity & Live Stats Hero Card
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(350)) + slideInVertically(tween(350)) { it / 5 },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(ProfileCard)
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                listOf(AccentCyan, AccentBlue, AccentViolet),
                            ),
                            shape = RoundedCornerShape(26.dp),
                        )
                        .padding(22.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // Rotating Neon Avatar Ring
                        Box(
                            modifier = Modifier.size(96.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .rotate(ringRotation)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.sweepGradient(
                                            listOf(AccentCyan, AccentBlue, AccentViolet, AccentPink, AccentCyan),
                                        ),
                                    ),
                            )
                            Box(
                                modifier = Modifier
                                    .size(88.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF070B18)),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (!state.avatarUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = state.avatarUrl,
                                        contentDescription = "Profile Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape),
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_beatwave_logo),
                                        contentDescription = "BeatWave Avatar",
                                        modifier = Modifier.size(58.dp),
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = state.displayName,
                                fontFamily = CyberDisplayFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Icon(
                                imageVector = Icons.Rounded.Verified,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(18.dp),
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = state.bioTagline,
                            fontFamily = CyberUiFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFFA4B8D8),
                            textAlign = TextAlign.Center,
                        )

                        Spacer(Modifier.height(12.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            StatusPill(
                                text = state.acousticRank,
                                accent = AccentCyan,
                            )
                            StatusPill(
                                text = if (state.isYtConnected) "YT MUSIC SYNCED" else "LOCAL CYBER MODE",
                                accent = if (state.isYtConnected) AccentEmerald else AccentViolet,
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        // 4 Essential User Telemetry Counters
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            ProfileStatColumn(
                                icon = Icons.Rounded.PlayCircle,
                                value = state.totalPlays.toString(),
                                label = "Plays",
                                accent = AccentCyan,
                            )
                            ProfileStatColumn(
                                icon = Icons.Rounded.MusicNote,
                                value = state.uniqueSongs.toString(),
                                label = "Tracks",
                                accent = AccentViolet,
                            )
                            ProfileStatColumn(
                                icon = Icons.Rounded.CloudDownload,
                                value = state.downloadedCount.toString(),
                                label = "Vault",
                                accent = AccentEmerald,
                            )
                            ProfileStatColumn(
                                icon = Icons.Rounded.Schedule,
                                value = formatListenDuration(state.totalListenMs),
                                label = "Listened",
                                accent = AccentBlue,
                            )
                        }
                    }
                }
            }

            // 2. Listening Journey & Active Vibe Capsule
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ProfileCard)
                    .border(1.dp, AccentBlue.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                    .padding(18.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(AccentCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Headphones,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "LISTENING TELEMETRY",
                                fontFamily = CyberMonoFont,
                                fontSize = 11.sp,
                                letterSpacing = 1.3.sp,
                                color = AccentCyan,
                            )
                            Text(
                                text = "Acoustic Journey & Vibe",
                                fontFamily = CyberUiFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color.White,
                            )
                        }
                        Text(
                            text = "${state.savedPlaylistsCount} Playlists",
                            fontFamily = CyberMonoFont,
                            fontSize = 11.sp,
                            color = AccentViolet,
                        )
                    }

                    Text(
                        text = if (state.totalPlays > 0) {
                            "You have streamed ${state.totalPlays} tracks across ${state.uniqueArtists} artists (${formatBytes(state.downloadedBytes)} stored in offline vault)."
                        } else {
                            "Your BeatWave cyber telemetry is armed and ready. Stream lossless tracks, tune 432 Hz harmonics, or build offline playlists to level up your Acoustic Rank."
                        },
                        fontFamily = CyberUiFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFFB8C7E0),
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        listOf(
                            "Lossless Cyber" to Icons.Rounded.Memory,
                            "Late Night 432Hz" to Icons.Rounded.AutoAwesome,
                            "High Energy Bass" to Icons.Rounded.Bolt,
                            "Deep Focus" to Icons.Rounded.GraphicEq,
                        ).forEach { (vibe, icon) ->
                            val isSelected = state.selectedVibe == vibe
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (isSelected) AccentCyan.copy(alpha = 0.20f) else ProfileCardHigh,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .border(
                                        1.dp,
                                        if (isSelected) AccentCyan else Color(0xFF25345C),
                                        RoundedCornerShape(50),
                                    )
                                    .clickable { viewModel.selectVibe(vibe) },
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) AccentCyan else Color(0xFF94A3B8),
                                        modifier = Modifier.size(14.dp),
                                    )
                                    Text(
                                        text = vibe,
                                        fontFamily = CyberUiFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) AccentCyan else Color(0xFFE2E8F0),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. BeatWave Quick Cyber Modules
            Text(
                text = "CYBER MODULES & ACCOUNT",
                fontFamily = CyberMonoFont,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp,
                color = AccentCyan,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp),
            )

            ProfileNavCard(
                icon = Icons.Rounded.Equalizer,
                accent = AccentCyan,
                title = "Cyber Sound Lab & 432Hz Tuner",
                subtitle = "Binaural Alpha synth, Neon Rain, Slowed & Nightcore pitch DSP",
                badge = "NEW",
                onClick = onOpenSoundLab,
            )

            ProfileNavCard(
                icon = Icons.Rounded.LibraryMusic,
                accent = AccentEmerald,
                title = "Downloads & Offline Vault",
                subtitle = "${state.downloadedCount} offline track(s) • ${formatBytes(state.downloadedBytes)} local storage",
                onClick = onOpenDownloads,
            )

            ProfileNavCard(
                icon = Icons.Rounded.CloudDone,
                accent = AccentBlue,
                title = if (state.isYtConnected) "YouTube Music Connected" else "Connect YouTube Music",
                subtitle = if (state.isYtConnected) {
                    "Synced ${state.ytHandle ?: state.displayName} • Playlists & Personalized Recommendations"
                } else {
                    "Sign in to sync playlists, liked songs & cloud recommendations"
                },
                onClick = onOpenYouTubeLogin,
            )

            // 4. SETTINGS & APP CUSTOMIZATION INSIDE PROFILE
            Text(
                text = "CUSTOMIZATION & ENGINE SETTINGS",
                fontFamily = CyberMonoFont,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp,
                color = AccentViolet,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ProfileCard)
                    .border(
                        width = 1.3.dp,
                        brush = Brush.linearGradient(
                            listOf(AccentViolet.copy(alpha = 0.8f), AccentBlue.copy(alpha = 0.8f)),
                        ),
                        shape = RoundedCornerShape(24.dp),
                    )
                    .clickable(onClick = onOpenSettings)
                    .padding(18.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(AccentViolet.copy(alpha = 0.18f))
                                .border(1.dp, AccentViolet.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = null,
                                tint = AccentViolet,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Settings & App Customization",
                                fontFamily = CyberUiFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White,
                            )
                            Text(
                                text = "Customize theme, Liquid Glass, app icon, 15-band EQ, AutoEQ & Bit-Perfect audio",
                                fontFamily = CyberUiFont,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Color(0xFFA4B8D8),
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                            contentDescription = null,
                            tint = AccentViolet,
                            modifier = Modifier.size(16.dp),
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        MiniCustomizationTag(Icons.Rounded.Palette, "Cyber Themes")
                        MiniCustomizationTag(Icons.Rounded.AutoAwesome, "Liquid Glass")
                        MiniCustomizationTag(Icons.Rounded.Tune, "15-Band PEQ")
                        MiniCustomizationTag(Icons.Rounded.Headphones, "USB DAC")
                    }
                }
            }

            // 5. LAST ITEM AT BOTTOM OF PROFILE: ABOUT BEATWAVE
            Text(
                text = "SYSTEM ARCHITECTURE // CREATOR",
                fontFamily = CyberMonoFont,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp,
                color = AccentPink,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF0D142C), Color(0xFF161132)),
                        ),
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            listOf(AccentCyan, AccentViolet, AccentPink),
                        ),
                        shape = RoundedCornerShape(24.dp),
                    )
                    .clickable(onClick = onOpenAbout)
                    .padding(18.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF070B18))
                            .border(1.5.dp, AccentCyan, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_beatwave_logo),
                            contentDescription = "BeatWave Logo",
                            modifier = Modifier.size(36.dp),
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = "About BeatWave",
                                fontFamily = CyberDisplayFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color.White,
                            )
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Engineered by mokka coding • @dark.shadow_4531 • Cyber Specs",
                            fontFamily = CyberUiFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = AccentCyan.copy(alpha = 0.9f),
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                        contentDescription = "Open About",
                        tint = AccentCyan,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    if (showEditDialog) {
        EditProfileDialog(
            initialName = state.displayName,
            initialBio = state.bioTagline,
            onDismiss = { showEditDialog = false },
            onSave = { name, bio ->
                viewModel.updateProfileIdentity(name, bio)
                showEditDialog = false
            },
        )
    }
}

@Composable
private fun ProfileStatColumn(
    icon: ImageVector,
    value: String,
    label: String,
    accent: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = value,
            fontFamily = CyberDisplayFont,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.White,
        )
        Text(
            text = label,
            fontFamily = CyberMonoFont,
            fontSize = 11.sp,
            color = Color(0xFF94A3B8),
        )
    }
}

@Composable
private fun StatusPill(
    text: String,
    accent: Color,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(accent.copy(alpha = 0.14f))
            .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 5.dp),
    ) {
        Text(
            text = text,
            fontFamily = CyberMonoFont,
            fontSize = 10.sp,
            letterSpacing = 1.1.sp,
            color = accent,
        )
    }
}

@Composable
private fun ProfileNavCard(
    icon: ImageVector,
    accent: Color,
    title: String,
    subtitle: String,
    badge: String? = null,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(ProfileCard)
            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = title,
                        fontFamily = CyberUiFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White,
                    )
                    if (badge != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AccentPink.copy(alpha = 0.22f))
                                .border(1.dp, AccentPink, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = badge,
                                fontFamily = CyberMonoFont,
                                fontSize = 9.sp,
                                color = AccentPink,
                            )
                        }
                    }
                }
                Text(
                    text = subtitle,
                    fontFamily = CyberUiFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                contentDescription = null,
                tint = accent.copy(alpha = 0.8f),
                modifier = Modifier.size(15.dp),
            )
        }
    }
}

@Composable
private fun MiniCustomizationTag(
    icon: ImageVector,
    label: String,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ProfileCardHigh)
            .padding(horizontal = 9.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentCyan,
            modifier = Modifier.size(13.dp),
        )
        Text(
            text = label,
            fontFamily = CyberMonoFont,
            fontSize = 10.sp,
            color = Color(0xFFCBD5E1),
        )
    }
}

@Composable
private fun EditProfileDialog(
    initialName: String,
    initialBio: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    var bio by remember { mutableStateOf(initialBio) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0E152D),
        title = {
            Text(
                text = "EDIT CYBER IDENTITY",
                fontFamily = CyberDisplayFont,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Listener Callsign / Name", fontFamily = CyberUiFont) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = Color(0xFF2D4673),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = AccentCyan,
                        unfocusedLabelColor = Color(0xFF94A3B8),
                    ),
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Status / Vibe Tagline", fontFamily = CyberUiFont) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentViolet,
                        unfocusedBorderColor = Color(0xFF2D4673),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = AccentViolet,
                        unfocusedLabelColor = Color(0xFF94A3B8),
                    ),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, bio) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentCyan,
                    contentColor = Color(0xFF04101E),
                ),
            ) {
                Text("SAVE", fontFamily = CyberMonoFont, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", fontFamily = CyberMonoFont, color = Color(0xFF94A3B8))
            }
        },
    )
}

private fun formatListenDuration(totalMs: Long): String {
    val totalMinutes = (totalMs / 60_000L).coerceAtLeast(0L)
    val hours = totalMinutes / 60L
    val mins = totalMinutes % 60L
    return "${hours}h ${mins}m"
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0L) return "0 MB"
    val mb = bytes.toDouble() / (1024.0 * 1024.0)
    return if (mb >= 1024.0) {
        String.format("%.1f GB", mb / 1024.0)
    } else {
        String.format("%.1f MB", mb)
    }
}
