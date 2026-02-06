package com.qualcomm.alvion.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeTab(
    onStart: () -> Unit,
    onSettings: () -> Unit,
    onSummary: () -> Unit,
) {
    Column(
        modifier =
            Modifier
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
                    onClick = onStart,
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
}
