package com.example.dynamic_forms_project.data

/**
 * UI Schema - Controls layout, grouping, and conditional visibility
 */
data class UiSchema(
    val layout: LayoutType = LayoutType.VERTICAL,
    val columns: Int = 1,
    val groups: List<FieldGroup> = emptyList(),
    val fieldOrder: List<String> = emptyList(),
    val spacing: Int = 16,
    val rules: List<ConditionalRule> = emptyList()
)

data class FieldGroup(
    val title: String,
    val fields: List<String>,
    val collapsible: Boolean = false,
    val defaultExpanded: Boolean = true
)

data class ConditionalRule(
    val field: String,              // Field to show/hide
    val effect: RuleEffect,         // SHOW, HIDE, ENABLE, DISABLE
    val condition: Condition        // When to apply
)

data class Condition(
    val field: String,              // Field to watch
    val operator: ConditionOperator,
    val value: Any?
)

enum class LayoutType {
    VERTICAL,   // Single column
    GRID        // Multi-column grid
}

enum class RuleEffect {
    SHOW,
    HIDE,
    ENABLE,
    DISABLE
}

enum class ConditionOperator {
    EQUALS,
    NOT_EQUALS,
    CONTAINS,
    GREATER_THAN,
    LESS_THAN,
    IS_EMPTY,
    NOT_EMPTY
}
