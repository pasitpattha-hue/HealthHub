package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.theme.ChillaxRoundedDisplayFamily
import com.example.ui.theme.CrystalIce
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    currentWebhookUrl: String,
    onSaveUrl: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var urlInput by remember { mutableStateOf(currentWebhookUrl) }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = ObsidianCard,
            modifier = Modifier.imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "⚙️ GAS v4.0 Webhook Config",
                    color = CrystalIce,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Configure your Google Apps Script Web App URL for 2026_HealthHub Google Sheets sync.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    label = { Text("GAS Web App URL", color = TextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gas_webhook_url_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = CrystalIce,
                        unfocusedBorderColor = TextSecondary
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CrystalButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        isPrimary = false,
                        modifier = Modifier.weight(1f),
                        testTag = "settings_cancel_button"
                    )
                    CrystalButton(
                        text = "Save Endpoint",
                        onClick = {
                            onSaveUrl(urlInput)
                            onDismiss()
                        },
                        isPrimary = true,
                        modifier = Modifier.weight(1f),
                        testTag = "settings_save_button"
                    )
                }
            }
        }
    }
}
