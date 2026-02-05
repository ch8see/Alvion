package com.qualcomm.alvion.feature.session

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.face.Face
import com.qualcomm.alvion.core.ui.components.CameraPreviewBox
import com.qualcomm.alvion.core.ui.components.GraphicOverlay
import com.qualcomm.alvion.feature.facedetection.FaceDetectionAnalyzer

@Composable
fun SessionScreen(onEnd: () -> Unit) {
    var soundEnabled by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val emergencyNumber = "9513034883"

    val mediaPlayer: MediaPlayer =
        remember {
            val uri: Uri? =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

            if (uri == null) {
                MediaPlayer()
            } else {
                MediaPlayer().apply {
                    try {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build(),
                        )
                        setDataSource(context, uri)
                        isLooping = false // Make sure the sound doesn't loop
                        prepare()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

    var faces by remember { mutableStateOf<List<Face>>(emptyList()) }

    val faceDetectionAnalyzer =
        remember {
            FaceDetectionAnalyzer(
                onFacesDetected = { detectedFaces ->
                    faces = detectedFaces
                },
                onDrowsy = {
                    if (soundEnabled) {
                        if (!mediaPlayer.isPlaying) {
                            mediaPlayer.start()
                        }
                    }
                },
                onDistracted = {
                    if (soundEnabled) {
                        if (!mediaPlayer.isPlaying) {
                            mediaPlayer.start()
                        }
                    }
                },
            )
        }

    DisposableEffect(Unit) {
        onDispose {
            try {
                if (mediaPlayer.isPlaying) mediaPlayer.stop()
            } catch (_: IllegalStateException) {
            }
            mediaPlayer.release()
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(16.dp),
        ) {
            CameraPreviewBox(
                modifier = Modifier.fillMaxSize(),
                useFrontCamera = true,
                analyzer = faceDetectionAnalyzer,
                faces = faces, // Pass the faces to the CameraPreviewBox
                graphicOverlay = { detectedFaces ->
                    GraphicOverlay(faces = detectedFaces)
                },
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            ElevatedCard(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(imageVector = Icons.Filled.Info, contentDescription = null)
                    Column {
                        Text("Status Indicator", style = MaterialTheme.typography.labelLarge)
                        Text(if (faces.isNotEmpty()) "Face Detected" else "No Face", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            ElevatedCard(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                onClick = { makeEmergencyCall(context, emergencyNumber) },
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(imageVector = Icons.Filled.Phone, contentDescription = "Emergency Call")
                    Column {
                        Text("Emergency", style = MaterialTheme.typography.labelLarge)
                        Text("Tap to call")
                    }
                }
            }
        }

        ElevatedCard(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Notification Type", style = MaterialTheme.typography.titleMedium)

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AssistChip(
                        onClick = { soundEnabled = !soundEnabled },
                        label = { Text("Sound") },
                        leadingIcon = { Icon(Icons.Filled.VolumeUp, contentDescription = "Sound") },
                        colors =
                            AssistChipDefaults.assistChipColors(
                                containerColor = if (soundEnabled) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            ),
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        ElevatedButton(onClick = onEnd, modifier = Modifier.fillMaxWidth()) {
            Text("End Session")
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
