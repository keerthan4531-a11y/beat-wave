package com.lastwave.app.ui.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.lastwave.app.R
import androidx.compose.ui.unit.dp
import com.lastwave.app.ui.common.adaptiveContentWidth
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.lastwave.app.ui.theme.CyberDisplayFont
import com.lastwave.app.ui.theme.ExpressivePillShape

/**
 * YouTube Music-first onboarding.
 *
 * First launch shows exactly two primary options:
 *  1. "Login with YouTube Music" — launches the existing YouTube Music web
 *     login flow (YouTubeLoginScreen / YtMusicAuthManager) to capture
 *     authentication cookies. Upon success the caller navigates to MainShell.
 *  2. "Continue as Guest" — persists guest mode (account-free) and enters
 *     MainShell immediately with local-first recommendations.
 *
 * Last.fm is intentionally NOT part of this path anymore. It lives in
 * Settings → Integrations / Scrobbling and is fully optional.
 */
@Composable
fun LoginScreen(
    onLoginWithYouTube: () -> Unit,
    onContinueAsGuest: () -> Unit,
    onRestoreBackupAndSignIn: ((String) -> Unit)? = null,
    onDismissError: (() -> Unit)? = null,
    errorMessage: String? = null,
    isBusy: Boolean = false,
    onOpenDownloads: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    var restoreReadError by remember { mutableStateOf<String?>(null) }
    val restoreBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null && onRestoreBackupAndSignIn != null) {
            val content = runCatching {
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    ?: error("Could not read this backup")
            }
            content.onSuccess {
                restoreReadError = null
                onRestoreBackupAndSignIn(it)
            }.onFailure {
                restoreReadError = it.message ?: "Could not read this backup"
            }
        }
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .adaptiveContentWidth(maxWidth = 480.dp)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .size(92.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0A0F22))
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color(0xFF00F2FE),
                                    Color(0xFF3B82F6),
                                    Color(0xFFA855F7),
                                    Color(0xFF00F2FE),
                                ),
                            ),
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_beatwave_logo),
                        contentDescription = "BeatWave Logo",
                        modifier = Modifier.size(64.dp),
                    )
                }
                Text(
                    text = "BEATWAVE",
                    fontFamily = CyberDisplayFont,
                    fontWeight = FontWeight.Black,
                    fontSize = 26.sp,
                    letterSpacing = 2.5.sp,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    stringResource(R.string.login_tagline),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(32.dp))

                // 1. Primary: YouTube Music login.
                Button(
                    onClick = onLoginWithYouTube,
                    enabled = !isBusy,
                    shape = ExpressivePillShape,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                ) {
                    Icon(
                        Icons.Filled.PlayCircle,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Text(stringResource(R.string.login_yt))
                }
                Spacer(Modifier.height(12.dp))

                // 2. Primary: Guest mode (account-free, local-first).
                OutlinedButton(
                    onClick = onContinueAsGuest,
                    enabled = !isBusy,
                    shape = ExpressivePillShape,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                ) {
                    Icon(
                        Icons.Filled.PersonOutline,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Text(stringResource(R.string.login_guest))
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    stringResource(R.string.login_guest_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                if (onRestoreBackupAndSignIn != null) {
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { restoreBackupLauncher.launch(arrayOf("application/json", "text/plain", "*/*")) },
                        enabled = !isBusy,
                        shape = ExpressivePillShape,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                    ) {
                        Icon(
                            Icons.Filled.CloudDownload,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        Text(stringResource(R.string.login_restore))
                    }
                }

                if (onOpenDownloads != null) {
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onOpenDownloads) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 6.dp),
                        )
                        Text(stringResource(R.string.login_downloads))
                    }
                }

                val visibleError = errorMessage ?: restoreReadError
                if (visibleError != null) {
                    Spacer(Modifier.height(16.dp))
                    Text(visibleError, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(4.dp))
                    if (onDismissError != null) {
                        TextButton(onClick = {
                            restoreReadError = null
                            onDismissError()
                        }) { Text(stringResource(R.string.gen_dismiss)) }
                    }
                }
            }
        }
    }
}
