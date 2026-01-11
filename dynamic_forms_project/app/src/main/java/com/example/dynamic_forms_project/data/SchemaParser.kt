package com.example.dynamic_forms_project.data

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion

/**
 * Parses JSON Schema and validates form data using NetworkNT library
 */
class SchemaParser {
    
    private val mapper = ObjectMapper()
    private val schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7)
    
    private var schemaJson: String = ""
    private var compiledSchema: com.networknt.schema.JsonSchema? = null
    
    fun parse(json: String): List<FormField> {
        schemaJson = json
        val root = mapper.readTree(json)
        val properties = root["properties"] ?: return emptyList()
        val required = root["required"]?.map { it.asText() } ?: emptyList()
        
        // Compile schema for validation
        compiledSchema = schemaFactory.getSchema(json)
        
        return properties.fields().asSequence().map { (name, prop) ->
            FormField(
                name = name,
                type = parseType(prop),
                title = prop["title"]?.asText() ?: name.capitalize(),
                isRequired = name in required,
                options = parseEnumOptions(prop),
                defaultValue = parseDefault(prop),
                format = prop["format"]?.asText(),
                minLength = prop["minLength"]?.asInt(),
                maxLength = prop["maxLength"]?.asInt()
            )
        }.toList()
    }
    
    private fun parseType(prop: JsonNode): FieldType {
        val type = prop["type"]?.asText()
        val format = prop["format"]?.asText()
        val maxLength = prop["maxLength"]?.asInt()
        
        // Check for array with enum (multiselect)
        if (type == "array" && prop["items"]?.has("enum") == true) {
            return FieldType.MULTISELECT
        }
        
        // Check for enum (dropdown)
        if (prop.has("enum")) return FieldType.DROPDOWN
        
        // Check format for string types
        if (type == "string") {
            return when (format) {
                "email" -> FieldType.EMAIL
                "uri", "url" -> FieldType.URL
                "password" -> FieldType.PASSWORD
                "date" -> FieldType.DATE
                "time" -> FieldType.TIME
                else -> {
                    // Large text area for long strings
                    if (maxLength != null && maxLength > 200) {
                        FieldType.TEXTAREA
                    } else {
                        FieldType.TEXT
                    }
                }
            }
        }
        
        return when (type) {
            "integer", "number" -> FieldType.NUMBER
            "boolean" -> FieldType.BOOLEAN
            else -> FieldType.TEXT
        }
    }
    
    private fun parseEnumOptions(prop: JsonNode): List<String>? {
        // Direct enum
        prop["enum"]?.let { enumNode ->
            return enumNode.map { it.asText() }
        }
        
        // Array with items.enum (for multiselect)
        prop["items"]?.["enum"]?.let { enumNode ->
            return enumNode.map { it.asText() }
        }
        
        return null
    }
    
    private fun parseDefault(prop: JsonNode): Any? {
        val default = prop["default"] ?: return null
        return when {
            default.isBoolean -> default.asBoolean()
            default.isInt -> default.asInt()
            default.isDouble -> default.asDouble()
            else -> default.asText()
        }
    }
    
    /**
     * Validates form data against the schema
     * @return Map of field name to error message (empty if valid)
     */
    fun validate(formData: Map<String, Any?>): Map<String, String> {
        val schema = compiledSchema ?: return emptyMap()
        
        // Convert form data to JSON
        val dataJson = mapper.writeValueAsString(formData)
        val dataNode = mapper.readTree(dataJson)
        
        // Validate using NetworkNT
        val errors = schema.validate(dataNode)
        
        // Parse errors into field -> message map
        return errors.mapNotNull { error ->
            // Extract field name from message (e.g., "$.username: ...")
            val message = error.message
            val fieldName = if (message.contains("$.")) {
                message.substringAfter("$.").substringBefore(":").trim()
            } else {
                message.substringBefore(":").trim()
            }
            
            if (fieldName.isNotEmpty()) {
                fieldName to formatError(message)
            } else {
                null
            }
        }.toMap()
    }
    
    private fun formatError(message: String): String {
        // Clean up error messages for better UX
        return message
            .replace("$.","")
            .replace("string", "")
            .replace("integer", "number")
            .trim()
            .replaceFirstChar { it.uppercase() }
    }
    
    fun getRawSchema(): String = schemaJson
    
    private fun String.capitalize() = replaceFirstChar { it.uppercase() }
}
