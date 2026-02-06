package com.qualcomm.alvion.feature.home

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.vision.face.Face
import com.qualcomm.alvion.feature.home.components.CameraPreviewBox
import com.qualcomm.alvion.feature.home.util.FaceDetectionAnalyzer
import com.qualcomm.alvion.feature.home.components.GraphicOverlay

@Composable
fun HomeTab(
    onSettings: () -> Unit,
    onSummary: () -> Unit,
) {
    var isSessionActive by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Home Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "Driver Monitoring",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        "Detect drowsiness & distraction with a clean, simple workflow.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                    Button(
                        onClick = { isSessionActive = true },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Start Live Session")
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(onClick = onSettings, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.Settings, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Settings")
                }
                OutlinedButton(onClick = onSummary, modifier = Modifier.weight(1f)) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Summary")
                }
            }

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("Quick Tips", style = MaterialTheme.typography.titleMedium)
                    AssistChip(onClick = { }, label = { Text("Camera permission required") })
                    Text(
                        "Run a simulation first if the camera pipeline isn’t ready. " +
                            "Use Settings to adjust sensitivity and feature toggles.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp,
                    )
                }
            }
        }

        // Animated Camera Session Overlay
        AnimatedVisibility(
            visible = isSessionActive,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        ) {
            CameraSessionLayer(onEnd = { isSessionActive = false })
        }
    }
}

@Composable
private fun CameraSessionLayer(onEnd: () -> Unit) {
    val context = LocalContext.current
    var soundEnabled by remember { mutableStateOf(false) }
    var faces by remember { mutableStateOf<List<Face>>(emptyList()) }

    val mediaPlayer = remember {
        val uri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        if (uri == null) MediaPlayer() else MediaPlayer().apply {
            try {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(context, uri)
                isLooping = false
                prepare()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    val faceDetectionAnalyzer = remember {
        FaceDetectionAnalyzer(
            onFacesDetected = { faces = it },
            onDrowsy = { if (soundEnabled && !mediaPlayer.isPlaying) mediaPlayer.start() },
            onDistracted = { if (soundEnabled && !mediaPlayer.isPlaying) mediaPlayer.start() },
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            try { if (mediaPlayer.isPlaying) mediaPlayer.stop() } catch (_: Exception) {}
            mediaPlayer.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(24.dp),
        ) {
            CameraPreviewBox(
                modifier = Modifier.fillMaxSize(),
                useFrontCamera = true,
                analyzer = faceDetectionAnalyzer,
                faces = faces,
                graphicOverlay = { GraphicOverlay(faces = it) },
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            SessionInfoCard(
                icon = Icons.Filled.Face,
                title = "Status",
                value = if (faces.isNotEmpty()) "Face Detected" else "No Face",
                modifier = Modifier.weight(1f)
            )
            SessionInfoCard(
                icon = Icons.Filled.Phone,
                title = "Emergency",
                value = "Tap to call",
                modifier = Modifier.weight(1f),
                onClick = { makeEmergencyCall(context, "9513034883") }
            )
        }

        ElevatedCard(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Enable Audio Alerts", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = soundEnabled, onCheckedChange = { soundEnabled = it })
            }
        }

        Button(
            onClick = onEnd,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("End Session", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SessionInfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick ?: {}
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column {
                Text(title, style = MaterialTheme.typography.labelMedium)
                Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

internal fun makeEmergencyCall(
    context: Context,
    emergencyNumber: String,
) {
    if (emergencyNumber.isBlank()) return
    val intent =
        Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$emergencyNumber")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
