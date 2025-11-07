package com.example.alvion.ui

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
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

@Composable
fun SessionScreen(onEnd: () -> Unit) {
    // Notify dialog state
    var showNotifyDialog: Boolean by remember { mutableStateOf(false) }
    var notifyClicks: Int by remember { mutableIntStateOf(0) }

    // Sound chip + dialog state
    var showSoundDialog: Boolean by remember { mutableStateOf(false) }
    var soundEnabled: Boolean by remember { mutableStateOf(false) }

    // ---- Looping MediaPlayer using the system notification tone (no raw/ file) ----
    val context = LocalContext.current
    val mediaPlayer: MediaPlayer = remember {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            setDataSource(context, uri)
            isLooping = true   // <-- keep playing until we stop
            prepare()          // prepare once up front
        }
    }
    // Ensure we stop/release when the composable leaves
    DisposableEffect(Unit) {
        onDispose {
            try {
                if (mediaPlayer.isPlaying) mediaPlayer.stop()
            } catch (_: IllegalStateException) { }
            mediaPlayer.release()
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
        // Camera/user panel (placeholder)
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(96.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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

            ElevatedCard(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Phone, contentDescription = null)
                    Column {
                        Text("Emergency", style = MaterialTheme.typography.labelLarge)
                        Text("Call")
                    }
                }
            }
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

                var vibrateEnabled: Boolean by remember { mutableStateOf(true) }
                var notifyEnabled: Boolean by remember { mutableStateOf(false) }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AssistChip(
                        onClick = { vibrateEnabled = !vibrateEnabled },
                        label = { Text("Vibrate") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Filled.Notifications, contentDescription = null)
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
                                }
                            } catch (_: IllegalStateException) { }
                            showSoundDialog = true
                        },
                        label = { Text("Sound") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Filled.Notifications, contentDescription = null)
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
                            Icon(imageVector = Icons.Filled.Notifications, contentDescription = null)
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
            icon = { Icon(imageVector = Icons.Filled.Notifications, contentDescription = null) }
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
                        } catch (_: IllegalStateException) { }
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
            icon = { Icon(imageVector = Icons.Filled.Notifications, contentDescription = null) }
        )
    }
}




