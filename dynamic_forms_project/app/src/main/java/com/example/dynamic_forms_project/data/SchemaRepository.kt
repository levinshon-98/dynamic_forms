package com.example.dynamic_forms_project.data

import android.content.Context
import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
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
    
    private val mapper = ObjectMapper()
    
    suspend fun getAllSchemas(): Result<List<SchemaMetadata>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📡 Fetching schemas list from: $ALL_SCHEMAS_URL")
            
            val request = Request.Builder()
                .url(ALL_SCHEMAS_URL)
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            
            Log.d(TAG, "📥 Response Code: ${response.code}")
            Log.d(TAG, "📥 Response: $responseBody")
            
            if (response.isSuccessful && responseBody.isNotEmpty()) {
                val jsonNode = mapper.readTree(responseBody)
                
                if (jsonNode["status"]?.asText() == "success") {
                    val schemas = mutableListOf<SchemaMetadata>()
                    jsonNode["data"]?.forEach { item ->
                        schemas.add(SchemaMetadata(
                            id = item["id"]?.asText() ?: "",
                            name = item["name"]?.asText() ?: "",
                            schema = emptyMap(), // We'll load full schema separately
                            createdAt = item["createdAt"]?.asText() ?: "",
                            updatedAt = item["updatedAt"]?.asText() ?: ""
                        ))
                    }
                    Log.d(TAG, "✅ Loaded ${schemas.size} schemas")
                    return@withContext Result.success(schemas)
                }
            }
            
            Log.w(TAG, "⚠️ Failed to load schemas, using fallback")
            Result.success(listOf(
                SchemaMetadata(
                    id = "fallback",
                    name = "ברירת מחדל",
                    schema = emptyMap(),
                    createdAt = "",
                    updatedAt = ""
                )
            ))
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error loading schemas: ${e.message}", e)
            Result.success(listOf(
                SchemaMetadata(
                    id = "fallback",
                    name = "ברירת מחדל",
                    schema = emptyMap(),
                    createdAt = "",
                    updatedAt = ""
                )
            ))
        }
    }
    
    suspend fun loadSchemaByName(name: String): Result<String> = withContext(Dispatchers.IO) {
        if (name == "fallback") {
            return@withContext loadLocalSchema()
        }
        
        try {
            val url = "$API_URL?name=$name"
            Log.d(TAG, "📡 Loading schema: $url")
            
            val request = Request.Builder()
                .url(url)
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            
            Log.d(TAG, "📥 Response Code: ${response.code}")
            Log.d(TAG, "📥 Response Length: ${responseBody.length} chars")
            
            if (response.isSuccessful) {
                if (responseBody.contains("properties") && !responseBody.contains("\"status\":\"error\"")) {
                    Log.d(TAG, "✅ Schema loaded successfully")
                    return@withContext Result.success(responseBody)
                } else {
                    Log.w(TAG, "⚠️ Invalid schema response: $responseBody")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error loading schema: ${e.message}", e)
        }
        
        Log.d(TAG, "📂 Using fallback schema")
        loadLocalSchema()
    }
    
    suspend fun loadSchema(): Result<String> = withContext(Dispatchers.IO) {
        loadLocalSchema()
    }
    
    private fun loadLocalSchema(): Result<String> {
        return try {
            val json = context.assets
                .open("fallback_schema.json")
                .bufferedReader()
                .use { it.readText() }
            Log.d(TAG, "✅ Loaded fallback schema (${json.length} chars)")
            Result.success(json)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error loading fallback: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    companion object {
        private const val TAG = "SchemaRepository"
        private const val API_URL = "https://me-west1-j17coupons.cloudfunctions.net/getJsonSchema"
        private const val ALL_SCHEMAS_URL = "https://me-west1-j17coupons.cloudfunctions.net/getAllJsonSchemas"
    }
}
