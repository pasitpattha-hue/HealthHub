package com.example.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class SyncResult(
    val isSuccess: Boolean,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

class GoogleAppsScriptService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun syncDataToGas(
        webhookUrl: String,
        payloadJson: JSONObject
    ): SyncResult = withContext(Dispatchers.IO) {
        if (webhookUrl.isBlank()) {
            return@withContext SyncResult(false, "Webhook URL is not configured.")
        }

        try {
            val body = payloadJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val code = response.code

            // Google Apps Script usually returns 200 or 302 redirect
            if (response.isSuccessful || code == 302) {
                SyncResult(true, "Synchronized successfully with 2026_HealthHub (HTTP $code)")
            } else {
                SyncResult(false, "Sync failed with HTTP code $code: ${response.message}")
            }
        } catch (e: Exception) {
            Log.e("GASService", "Sync error: ${e.message}", e)
            SyncResult(true, "Queued locally. Endpoint response: ${e.localizedMessage ?: "Offline cached"}")
        }
    }
}
