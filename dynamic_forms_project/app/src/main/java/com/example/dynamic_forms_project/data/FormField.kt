package com.example.dynamic_forms_project.data

/**
 * Represents a single form field parsed from JSON Schema
 */
data class FormField(
    val name: String,
    val type: FieldType,
    val title: String,
    val isRequired: Boolean,
    val options: List<String>? = null, // For enum/dropdown
    val defaultValue: Any? = null,
    val format: String? = null, // email, uri, date, time, password
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val description: String? = null // Description/help text
)

enum class FieldType {
    TEXT,           // Basic text input
    NUMBER,         // Integer/Number input
    BOOLEAN,        // Checkbox
    DROPDOWN,       // Single select (enum)
    EMAIL,          // Email input with validation
    URL,            // URL input
    PASSWORD,       // Password field (hidden text)
    DATE,           // Date picker
    TIME,           // Time picker
    TEXTAREA,       // Multi-line text
    MULTISELECT     // Multiple selection (array of enum)
}
