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

class GoogleAppsScriptService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    suspend fun syncPayloadToGas(webhookUrl: String, payload: JSONObject): Boolean = withContext(Dispatchers.IO) {
        if (webhookUrl.isBlank()) return@withContext false
        try {
            val body = payload.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build()
            client.newCall(request).execute().use { response ->
                response.isSuccessful || response.code in 200..399
            }
        } catch (e: Exception) {
            Log.w("GoogleAppsScriptService", "Offline or webhook unreachable, stored locally in Room: ${e.message}")
            false
        }
    }
}
