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
            val request = Request.Builder()
                .url(ALL_SCHEMAS_URL)
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            
            if (response.isSuccessful && responseBody.isNotEmpty()) {
                val jsonNode = mapper.readTree(responseBody)
                
                if (jsonNode["status"]?.asText() == "success") {
                    val schemas = mutableListOf<SchemaMetadata>()
                    
                    // Always add fallback first
                    schemas.add(SchemaMetadata(
                        id = "fallback",
                        name = "ברירת מחדל",
                        schema = emptyMap(),
                        createdAt = "",
                        updatedAt = ""
                    ))
                    
                    // Add server schemas
                    jsonNode["data"]?.forEach { item ->
                        schemas.add(SchemaMetadata(
                            id = item["id"]?.asText() ?: "",
                            name = item["name"]?.asText() ?: "",
                            schema = emptyMap(), // We'll load full schema separately
                            createdAt = item["createdAt"]?.asText() ?: "",
                            updatedAt = item["updatedAt"]?.asText() ?: ""
                        ))
                    }
                    return@withContext Result.success(schemas)
                }
            }
            
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
        if (name == "ברירת מחדל" || name == "fallback") {
            return@withContext loadLocalSchema()
        }
        
        try {
            // URL encode the name parameter to handle special characters
            val encodedName = java.net.URLEncoder.encode(name, "UTF-8")
            val url = "$API_URL?name=$encodedName"
            
            val request = Request.Builder()
                .url(url)
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            
            if (response.isSuccessful && responseBody.isNotEmpty()) {
                val jsonNode = mapper.readTree(responseBody)
                
                // Check if response has status field (server response format)
                if (jsonNode.has("status") && jsonNode["status"]?.asText() == "success") {
                    val dataNode = jsonNode["data"]
                    if (dataNode == null) {
                        return@withContext Result.failure(Exception("יש בעיה בסכמה: אין נתונים בתגובה"))
                    }
                    
                    // Handle both array and object formats
                    val schemaItem = when {
                        dataNode.isArray && dataNode.size() > 0 -> {
                            dataNode.get(0)
                        }
                        dataNode.isObject -> {
                            dataNode
                        }
                        else -> {
                            return@withContext Result.failure(Exception("יש בעיה בסכמה: פורמט data לא מזוהה"))
                        }
                    }
                    
                    if (schemaItem != null) {
                        // Extract schema from data item
                        val schemaNode = schemaItem.get("schema")
                        if (schemaNode != null) {
                            val schemaJson = mapper.writeValueAsString(schemaNode)
                            
                            // Validate that schema has required fields
                            if (schemaNode.has("type") && schemaNode.has("properties")) {
                                return@withContext Result.success(schemaJson)
                            } else {
                                return@withContext Result.failure(Exception("יש בעיה בסכמה: חסרים שדות חובה (type או properties)"))
                            }
                        } else {
                            return@withContext Result.failure(Exception("יש בעיה בסכמה: לא נמצא שדה schema בתגובה"))
                        }
                    } else {
                        return@withContext Result.failure(Exception("יש בעיה בסכמה: אובייקט הסכמה ריק"))
                    }
                } else if (jsonNode.has("properties") && !jsonNode.has("status")) {
                    // Direct schema format
                    return@withContext Result.success(responseBody)
                } else {
                    return@withContext Result.failure(Exception("יש בעיה בסכמה: תגובה לא תקינה מהשרת"))
                }
            }
            
            return@withContext Result.failure(Exception("יש בעיה בסכמה: שגיאה ${response.code} מהשרת"))
        } catch (e: Exception) {
            return@withContext Result.failure(Exception("יש בעיה בסכמה: ${e.message}"))
        }
        
        Result.failure(Exception("יש בעיה בסכמה: שגיאה לא צפויה"))
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
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    companion object {
        private const val TAG = "SchemaRepository"
        private const val API_URL = "https://me-west1-j17coupons.cloudfunctions.net/getJsonSchema"
        private const val ALL_SCHEMAS_URL = "https://me-west1-j17coupons.cloudfunctions.net/getAllJsonSchemas"
    }
}
