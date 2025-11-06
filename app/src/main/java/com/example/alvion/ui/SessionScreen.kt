package com.example.alvion.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SessionScreen(onEnd: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary) // keep current theme tone
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
                    Icons.Filled.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(96.dp)
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
                    Icon(Icons.Filled.Info, contentDescription = null)
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
                    Icon(Icons.Filled.Phone, contentDescription = null)
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

                var vibrate by remember { mutableStateOf(true) }
                var sound by remember { mutableStateOf(false) }
                var notify by remember { mutableStateOf(false) }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AssistChip(
                        onClick = { vibrate = !vibrate },
                        label = { Text("Vibrate") },
                        leadingIcon = { Icon(Icons.Filled.Notifications, contentDescription = null) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (vibrate)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    AssistChip(
                        onClick = { sound = !sound },
                        label = { Text("Sound") },
                        leadingIcon = { Icon(Icons.Filled.Notifications, contentDescription = null) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (sound)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    AssistChip(
                        onClick = { notify = !notify },
                        label = { Text("Notify") },
                        leadingIcon = { Icon(Icons.Filled.Notifications, contentDescription = null) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (notify)
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
        ) {
            Text("End Session")
        }
    }
}
