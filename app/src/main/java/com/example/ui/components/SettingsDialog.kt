package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HealthConstants
import com.example.ui.theme.CrystalIce
import com.example.ui.theme.EmeraldMist
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.SpecularBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun SettingsDialog(
    currentWebhookUrl: String,
    onSaveUrl: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var urlText by remember { mutableStateOf(currentWebhookUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Command Center Settings",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Google Apps Script Web App URL (2026_HealthHub)",
                    color = CrystalIce,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Text(
                    text = "Incoming health logs (meals, workouts, bodycomp, sleep) will sync automatically via HTTP POST to this endpoint.",
                    color = TextTertiary,
                    fontSize = 11.sp
                )

                OutlinedTextField(
                    value = urlText,
                    onValueChange = { urlText = it },
                    label = { Text("GAS Web App URL") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gas_url_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CrystalIce,
                        unfocusedBorderColor = SpecularBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    CrystalPill("Local DB: Room SQLite", EmeraldMist)
                    CrystalPill("GAS v4.0 Match: Active", CrystalIce)
                }

                Text(
                    text = "Reset to Default: ${HealthConstants.DEFAULT_GAS_URL}",
                    color = TextTertiary,
                    fontSize = 10.sp
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSaveUrl(urlText) },
                modifier = Modifier.testTag("save_settings_button")
            ) {
                Text("Save Endpoint", color = CrystalIce, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = ObsidianCard
    )
}
