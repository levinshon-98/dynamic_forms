package com.example.dynamic_forms_project.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dynamic_forms_project.data.FieldGroup
import com.example.dynamic_forms_project.data.FormField
import com.example.dynamic_forms_project.data.LayoutType
import com.example.dynamic_forms_project.data.UiSchema

/**
 * Renders form fields according to UI Schema layout
 */
@Composable
fun FormContent(
    fields: List<FormField>,
    formData: Map<String, Any?>,
    errors: Map<String, String>,
    uiSchema: UiSchema?,
    isFieldVisible: (String) -> Boolean,
    isFieldEnabled: (String) -> Boolean,
    onValueChange: (String, Any?) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = uiSchema?.spacing ?: 16
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.dp)
    ) {
        when {
            // Has groups
            uiSchema?.groups?.isNotEmpty() == true -> {
                RenderGroups(
                    groups = uiSchema.groups,
                    fields = fields,
                    formData = formData,
                    errors = errors,
                    isFieldVisible = isFieldVisible,
                    isFieldEnabled = isFieldEnabled,
                    onValueChange = onValueChange
                )
            }
            
            // Grid layout
            uiSchema?.layout == LayoutType.GRID && uiSchema.columns > 1 -> {
                RenderGrid(
                    fields = getOrderedFields(fields, uiSchema),
                    columns = uiSchema.columns,
                    formData = formData,
                    errors = errors,
                    isFieldVisible = isFieldVisible,
                    isFieldEnabled = isFieldEnabled,
                    onValueChange = onValueChange,
                    spacing = spacing
                )
            }
            
            // Vertical layout (default)
            else -> {
                RenderVertical(
                    fields = getOrderedFields(fields, uiSchema),
                    formData = formData,
                    errors = errors,
                    isFieldVisible = isFieldVisible,
                    isFieldEnabled = isFieldEnabled,
                    onValueChange = onValueChange
                )
            }
        }
    }
}

@Composable
private fun RenderGroups(
    groups: List<FieldGroup>,
    fields: List<FormField>,
    formData: Map<String, Any?>,
    errors: Map<String, String>,
    isFieldVisible: (String) -> Boolean,
    isFieldEnabled: (String) -> Boolean,
    onValueChange: (String, Any?) -> Unit
) {
    groups.forEach { group ->
        FieldGroupComponent(
            group = group,
            fields = fields,
            formData = formData,
            errors = errors,
            isFieldVisible = isFieldVisible,
            isFieldEnabled = isFieldEnabled,
            onValueChange = onValueChange
        )
    }
}

@Composable
private fun RenderGrid(
    fields: List<FormField>,
    columns: Int,
    formData: Map<String, Any?>,
    errors: Map<String, String>,
    isFieldVisible: (String) -> Boolean,
    isFieldEnabled: (String) -> Boolean,
    onValueChange: (String, Any?) -> Unit,
    spacing: Int
) {
    val visibleFields = fields.filter { isFieldVisible(it.name) }
    val rows = visibleFields.chunked(columns)
    
    Column(verticalArrangement = Arrangement.spacedBy(spacing.dp)) {
        rows.forEach { rowFields ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.dp)
            ) {
                rowFields.forEach { field ->
                    Box(modifier = Modifier.weight(1f)) {
                        DynamicField(
                            field = field,
                            value = formData[field.name],
                            error = errors[field.name],
                            onValueChange = { onValueChange(field.name, it) },
                            enabled = isFieldEnabled(field.name)
                        )
                    }
                }
                
                // Fill empty spaces in last row
                repeat(columns - rowFields.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun RenderVertical(
    fields: List<FormField>,
    formData: Map<String, Any?>,
    errors: Map<String, String>,
    isFieldVisible: (String) -> Boolean,
    isFieldEnabled: (String) -> Boolean,
    onValueChange: (String, Any?) -> Unit
) {
    fields.forEach { field ->
        if (isFieldVisible(field.name)) {
            DynamicField(
                field = field,
                value = formData[field.name],
                error = errors[field.name],
                onValueChange = { onValueChange(field.name, it) },
                enabled = isFieldEnabled(field.name)
            )
        }
    }
}

private fun getOrderedFields(fields: List<FormField>, uiSchema: UiSchema?): List<FormField> {
    val fieldOrder = uiSchema?.fieldOrder ?: return fields
    if (fieldOrder.isEmpty()) return fields
    
    val fieldMap = fields.associateBy { it.name }
    return fieldOrder.mapNotNull { fieldMap[it] } + fields.filter { it.name !in fieldOrder }
}
