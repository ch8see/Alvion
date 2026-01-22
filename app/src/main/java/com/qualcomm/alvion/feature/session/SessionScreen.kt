package com.qualcomm.alvion.feature.session

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.qualcomm.alvion.core.ui.components.CameraPreviewBox

@Composable
fun SessionScreen(onEnd: () -> Unit) {
    var showNotifyDialog by remember { mutableStateOf(false) }
    var notifyClicks by remember { mutableIntStateOf(0) }
    var showSoundDialog by remember { mutableStateOf(false) }
    var soundEnabled by remember { mutableStateOf(false) }
    var showVibrateDialog by remember { mutableStateOf(false) }
    var vibrateEnabled by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val emergencyNumber = "9513034883"

    val vibrator: Vibrator? = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    val mediaPlayer: MediaPlayer = remember {
        val uri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
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
                            .build()
                    )
                    setDataSource(context, uri)
                    isLooping = true
                    prepare()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                if (mediaPlayer.isPlaying) mediaPlayer.stop()
            } catch (_: IllegalStateException) {}
            mediaPlayer.release()
            vibrator?.cancel()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(16.dp)
        ) {
            CameraPreviewBox(modifier = Modifier.fillMaxSize(), useFrontCamera = true)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            ElevatedCard(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(imageVector = Icons.Filled.Info, contentDescription = null)
                    Column {
                        Text("Status Indicator", style = MaterialTheme.typography.labelLarge)
                        Text("Normal", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            ElevatedCard(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                onClick = { makeEmergencyCall(context, emergencyNumber) }
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
                var notifyEnabled by remember { mutableStateOf(false) }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AssistChip(
                        onClick = {
                            vibrateEnabled = true
                            showVibrateDialog = true
                            vibrator?.let { vib ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vib.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 400, 300, 400, 300), 0))
                                } else {
                                    @Suppress("DEPRECATION")
                                    vib.vibrate(longArrayOf(0, 400, 300, 400, 300), 0)
                                }
                            }
                        },
                        label = { Text("Vibrate") },
                        leadingIcon = { Icon(Icons.Filled.Vibration, contentDescription = "Vibrate") },
                        colors = AssistChipDefaults.assistChipColors(containerColor = if (vibrateEnabled) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                    )

                    AssistChip(
                        onClick = {
                            soundEnabled = true
                            try {
                                if (!mediaPlayer.isPlaying) {
                                    mediaPlayer.seekTo(0)
                                    mediaPlayer.start()
                                }
                            } catch (e: Exception) { e.printStackTrace() }
                            showSoundDialog = true
                        },
                        label = { Text("Sound") },
                        leadingIcon = { Icon(Icons.Filled.VolumeUp, contentDescription = "Sound") },
                        colors = AssistChipDefaults.assistChipColors(containerColor = if (soundEnabled) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                    )

                    AssistChip(
                        onClick = {
                            notifyEnabled = !notifyEnabled
                            notifyClicks += 1
                            showNotifyDialog = true
                        },
                        label = { Text("Notify") },
                        leadingIcon = { Icon(Icons.Filled.Notifications, contentDescription = null) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = if (notifyEnabled) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        ElevatedButton(onClick = onEnd, modifier = Modifier.fillMaxWidth()) {
            Text("End Session")
        }
    }

    if (showNotifyDialog) {
        AlertDialog(
            onDismissRequest = { showNotifyDialog = false },
            confirmButton = { TextButton(onClick = { showNotifyDialog = false }) { Text("OK") } },
            title = { Text("Notification sent") },
            text = { Text("Notify tapped $notifyClicks times.") },
            icon = { Icon(Icons.Filled.Notifications, contentDescription = null) }
        )
    }

    if (showSoundDialog) {
        AlertDialog(
            onDismissRequest = { showSoundDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    try { if (mediaPlayer.isPlaying) mediaPlayer.pause(); mediaPlayer.seekTo(0) } catch (_: Exception) {}
                    soundEnabled = false
                    showSoundDialog = false
                }) { Text("Turn off sound") }
            },
            dismissButton = { TextButton(onClick = { showSoundDialog = false }) { Text("Close") } },
            title = { Text("Sound") },
            text = { Text("Playing until you turn it off") }
        )
    }

    if (showVibrateDialog) {
        AlertDialog(
            onDismissRequest = { showVibrateDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    vibrator?.cancel()
                    vibrateEnabled = false
                    showVibrateDialog = false
                }) { Text("Turn off vibration") }
            },
            dismissButton = { TextButton(onClick = { showVibrateDialog = false }) { Text("Close") } },
            title = { Text("Vibration") },
            text = { Text("Phone is vibrating until you turn it off.") }
        )
    }
}

internal fun makeEmergencyCall(context: Context, emergencyNumber: String) {
    if (emergencyNumber.isBlank()) return
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$emergencyNumber")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try { context.startActivity(intent) } catch (e: Exception) { e.printStackTrace() }
}
