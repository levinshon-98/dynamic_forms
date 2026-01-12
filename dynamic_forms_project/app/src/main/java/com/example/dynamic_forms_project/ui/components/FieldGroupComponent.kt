package com.example.dynamic_forms_project.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dynamic_forms_project.data.FieldGroup
import com.example.dynamic_forms_project.data.FormField

/**
 * Collapsible group of form fields
 */
@Composable
fun FieldGroupComponent(
    group: FieldGroup,
    fields: List<FormField>,
    formData: Map<String, Any?>,
    errors: Map<String, String>,
    isFieldVisible: (String) -> Boolean,
    isFieldEnabled: (String) -> Boolean,
    onValueChange: (String, Any?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(group.defaultExpanded) }
    val groupFields = fields.filter { it.name in group.fields }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Group Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (group.collapsible) {
                            Modifier.clickable { expanded = !expanded }
                        } else Modifier
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                if (group.collapsible) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Group Fields
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    groupFields.forEach { field ->
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
            }
        }
    }
}
