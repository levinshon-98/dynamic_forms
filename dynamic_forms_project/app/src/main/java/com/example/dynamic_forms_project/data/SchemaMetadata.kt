package com.example.dynamic_forms_project.data

/**
 * Schema metadata from server
 */
data class SchemaListResponse(
    val status: String,
    val count: Int,
    val data: List<SchemaMetadata>
)

data class SchemaMetadata(
    val id: String,
    val name: String,
    val schema: Map<String, Any>,
    val createdAt: String,
    val updatedAt: String
)
