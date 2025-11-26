package com.example.alvion.ui

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.alvion.ui.theme.CameraPreviewBox

@Composable
fun SessionScreen(onEnd: () -> Unit) {
    // Notify dialog state
    var showNotifyDialog: Boolean by remember { mutableStateOf(false) }
    var notifyClicks: Int by remember { mutableIntStateOf(0) }

    // Sound chip + dialog state
    var showSoundDialog: Boolean by remember { mutableStateOf(false) }
    var soundEnabled: Boolean by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Vibrate dialog state
    var showVibrateDialog: Boolean by remember { mutableStateOf(false) }
    var vibrateEnabled: Boolean by remember { mutableStateOf(false) }

    // Vibrator instance (handles old + new APIs)
    val vibrator: Vibrator? = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    // Emergency call number
    val emergencyNumber = "9513034883" // replace with your own number if you want

    // ---- Looping MediaPlayer using the system notification tone (no raw/ file) ----
    val mediaPlayer: MediaPlayer = remember {
        val uri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        if (uri == null) {
            // If we can’t get any system sound, tell the user once
            Toast.makeText(context, "No system notification sound found", Toast.LENGTH_SHORT).show()
            MediaPlayer() // dummy, won't play
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
                    isLooping = true   // keep playing until we stop
                    prepare()          // prepare once up front
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        context,
                        "Failed to prepare sound: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Ensure we stop/release when the composable leaves
    DisposableEffect(Unit) {
        onDispose {
            try {
                if (mediaPlayer.isPlaying) mediaPlayer.stop()
            } catch (_: IllegalStateException) {
            }
            mediaPlayer.release()

            // Stop vibration if it's running
            vibrator?.cancel()
        }
    }
    // -----------------------------------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Camera/user panel - live preview
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(16.dp)
        ) {
            CameraPreviewBox(
                modifier = Modifier.fillMaxSize(),
                useFrontCamera = true
            )
        }

        // Status + Emergency row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ElevatedCard(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Info, contentDescription = null)
                    Column {
                        Text("Status Indicator", style = MaterialTheme.typography.labelLarge)
                        Text("Normal", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // ---- Emergency "call" card (opens dialer, user taps call) ----
            ElevatedCard(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                onClick = {
                    makeEmergencyCall(context, emergencyNumber)
                }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Phone,
                        contentDescription = "Emergency Call"
                    )
                    Column {
                        Text("Emergency", style = MaterialTheme.typography.labelLarge)
                        Text("Tap to call")
                    }
                }
            }
            // ---------------------------------------------------------------
        }

        // Notification type
        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Notification Type", style = MaterialTheme.typography.titleMedium)

                var notifyEnabled: Boolean by remember { mutableStateOf(false) }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Vibrate chip — start vibrating and show dialog
                    AssistChip(
                        onClick = {
                            vibrateEnabled = true
                            showVibrateDialog = true

                            vibrator?.let { vib ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    // pattern: vibrate, pause, vibrate... until we cancel
                                    val pattern = longArrayOf(0, 400, 300, 400, 300)
                                    val effect =
                                        VibrationEffect.createWaveform(pattern, 0) // repeat
                                    vib.vibrate(effect)
                                } else {
                                    @Suppress("DEPRECATION")
                                    vib.vibrate(longArrayOf(0, 400, 300, 400, 300), 0)
                                }
                            }
                        },
                        label = { Text("Vibrate") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Vibration,
                                contentDescription = "Vibrate"
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (vibrateEnabled)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    )

                    // SOUND chip — start looping; keep playing until "Turn off sound"
                    AssistChip(
                        onClick = {
                            soundEnabled = true // visual only

                            try {
                                if (!mediaPlayer.isPlaying) {
                                    mediaPlayer.seekTo(0)
                                    mediaPlayer.start()
                                    Toast.makeText(context, "Playing sound…", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    // already playing
                                    Toast.makeText(
                                        context,
                                        "Sound already playing",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: IllegalStateException) {
                                e.printStackTrace()
                                Toast.makeText(
                                    context,
                                    "Error starting sound: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            showSoundDialog = true
                        },
                        label = { Text("Sound") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.VolumeUp,
                                contentDescription = "Sound"
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (soundEnabled)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    )

                    // NOTIFY chip — shows count dialog every click
                    AssistChip(
                        onClick = {
                            notifyEnabled = !notifyEnabled
                            notifyClicks += 1
                            showNotifyDialog = true
                        },
                        label = { Text("Notify") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = null
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (notifyEnabled)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // End session
        ElevatedButton(
            onClick = onEnd,
            modifier = Modifier.fillMaxWidth()
        ) { Text("End Session") }
    }

    // Notify popup
    if (showNotifyDialog) {
        AlertDialog(
            onDismissRequest = { showNotifyDialog = false },
            confirmButton = {
                TextButton(onClick = { showNotifyDialog = false }) { Text("OK") }
            },
            title = { Text("Notification sent") },
            text = { Text("Notify tapped $notifyClicks time${if (notifyClicks == 1) "" else "s"}.") },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null
                )
            }
        )
    }

    // Sound popup with "Turn off sound"
    if (showSoundDialog) {
        AlertDialog(
            onDismissRequest = { showSoundDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        try {
                            if (mediaPlayer.isPlaying) mediaPlayer.pause()
                            mediaPlayer.seekTo(0)
                        } catch (_: IllegalStateException) {
                        }
                        soundEnabled = false
                        showSoundDialog = false
                    }
                ) { Text("Turn off sound") }
            },
            dismissButton = {
                TextButton(onClick = { showSoundDialog = false }) { Text("Close") }
            },
            title = { Text("Sound") },
            text = { Text("Playing until you turn it off") },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null
                )
            }
        )
    }

    // Vibrate popup with "Turn off vibration"
    if (showVibrateDialog) {
        AlertDialog(
            onDismissRequest = { showVibrateDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        vibrator?.cancel()
                        vibrateEnabled = false
                        showVibrateDialog = false
                    }
                ) { Text("Turn off vibration") }
            },
            dismissButton = {
                TextButton(onClick = { showVibrateDialog = false }) { Text("Close") }
            },
            title = { Text("Vibration") },
            text = { Text("Phone is vibrating until you turn it off.") },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Vibration,
                    contentDescription = null
                )
            }
        )
    }
}

// Helper to open the dialer with the emergency number (NO permission needed)
internal fun makeEmergencyCall(context: Context, emergencyNumber: String) {
    if (emergencyNumber.isBlank()) return

    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$emergencyNumber")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Unable to open dialer", Toast.LENGTH_SHORT).show()
    }
}

