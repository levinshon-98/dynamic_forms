package com.example.dynamic_forms_project.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * Repository for loading JSON Schema from API or local fallback
 */
class SchemaRepository(private val context: Context) {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
    
    suspend fun loadSchema(): Result<String> = withContext(Dispatchers.IO) {
        // Try API first
        try {
            val request = Request.Builder()
                .url(API_URL)
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                response.body?.string()?.let { json ->
                    // Check if it's a valid schema (not an error response)
                    if (json.contains("properties") && !json.contains("\"status\":\"error\"")) {
                        return@withContext Result.success(json)
                    }
                }
            }
        } catch (e: Exception) {
            // API failed, will use fallback
        }
        
        // Fallback to local schema
        loadLocalSchema()
    }
    
    private fun loadLocalSchema(): Result<String> {
        return try {
            val json = context.assets
                .open("fallback_schema.json")
                .bufferedReader()
                .use { it.readText() }
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    companion object {
        private const val API_URL = "https://me-west1-j17coupons.cloudfunctions.net/getJsonSchema?name=mySchema"
    }
}
