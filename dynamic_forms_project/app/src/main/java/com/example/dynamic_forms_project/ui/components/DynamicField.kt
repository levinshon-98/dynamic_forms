package com.example.dynamic_forms_project.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.dynamic_forms_project.data.FieldType
import com.example.dynamic_forms_project.data.FormField
import com.example.dynamic_forms_project.ui.theme.ErrorColor
import com.example.dynamic_forms_project.ui.theme.SuccessColor

/**
 * Renders a form field dynamically based on its type
 */
@Composable
fun DynamicField(
    field: FormField,
    value: Any?,
    error: String?,
    onValueChange: (Any?) -> Unit,
    modifier: Modifier = Modifier
) {
    val hasError = error != null
    val hasValue = value != null && value.toString().isNotBlank()
    
    Column(modifier = modifier.fillMaxWidth()) {
        when (field.type) {
            FieldType.TEXT -> TextFieldComponent(field, value, hasError, onValueChange)
            FieldType.NUMBER -> NumberFieldComponent(field, value, hasError, onValueChange)
            FieldType.BOOLEAN -> CheckboxComponent(field, value, onValueChange)
            FieldType.DROPDOWN -> DropdownComponent(field, value, hasError, onValueChange)
            FieldType.EMAIL -> EmailFieldComponent(field, value, hasError, onValueChange)
            FieldType.URL -> UrlFieldComponent(field, value, hasError, onValueChange)
            FieldType.PASSWORD -> PasswordFieldComponent(field, value, hasError, onValueChange)
            FieldType.DATE -> DateFieldComponent(field, value, hasError, onValueChange)
            FieldType.TIME -> TimeFieldComponent(field, value, hasError, onValueChange)
            FieldType.TEXTAREA -> TextAreaComponent(field, value, hasError, onValueChange)
            FieldType.MULTISELECT -> MultiSelectComponent(field, value, hasError, onValueChange)
        }
        
        // Error or success indicator
        if (hasError) {
            Row(
                modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = ErrorColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = error!!,
                    color = ErrorColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        } else if (hasValue && field.isRequired) {
            Row(
                modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SuccessColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Valid",
                    color = SuccessColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun TextFieldComponent(
    field: FormField,
    value: Any?,
    hasError: Boolean,
    onValueChange: (Any?) -> Unit
) {
    OutlinedTextField(
        value = value?.toString() ?: "",
        onValueChange = { onValueChange(it) },
        label = { 
            Text(buildLabel(field)) 
        },
        isError = hasError,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
private fun NumberFieldComponent(
    field: FormField,
    value: Any?,
    hasError: Boolean,
    onValueChange: (Any?) -> Unit
) {
    OutlinedTextField(
        value = value?.toString() ?: "",
        onValueChange = { input ->
            val filtered = input.filter { it.isDigit() }
            onValueChange(filtered.toIntOrNull())
        },
        label = { Text(buildLabel(field)) },
        isError = hasError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
private fun CheckboxComponent(
    field: FormField,
    value: Any?,
    onValueChange: (Any?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            checked = value as? Boolean ?: false,
            onCheckedChange = { onValueChange(it) }
        )
        Text(
            text = field.title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownComponent(
    field: FormField,
    value: Any?,
    hasError: Boolean,
    onValueChange: (Any?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = field.options ?: emptyList()
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = value?.toString() ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(buildLabel(field)) },
            isError = hasError,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Email Field
@Composable
private fun EmailFieldComponent(
    field: FormField,
    value: Any?,
    hasError: Boolean,
    onValueChange: (Any?) -> Unit
) {
    OutlinedTextField(
        value = value?.toString() ?: "",
        onValueChange = onValueChange,
        label = { Text(buildLabel(field)) },
        isError = hasError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier.fillMaxWidth()
    )
}

// URL Field
@Composable
private fun UrlFieldComponent(
    field: FormField,
    value: Any?,
    hasError: Boolean,
    onValueChange: (Any?) -> Unit
) {
    OutlinedTextField(
        value = value?.toString() ?: "",
        onValueChange = onValueChange,
        label = { Text(buildLabel(field)) },
        isError = hasError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
        modifier = Modifier.fillMaxWidth()
    )
}

// Password Field
@Composable
private fun PasswordFieldComponent(
    field: FormField,
    value: Any?,
    hasError: Boolean,
    onValueChange: (Any?) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    
    OutlinedTextField(
        value = value?.toString() ?: "",
        onValueChange = onValueChange,
        label = { Text(buildLabel(field)) },
        isError = hasError,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

// Date Field (simplified - using text input)
@Composable
private fun DateFieldComponent(
    field: FormField,
    value: Any?,
    hasError: Boolean,
    onValueChange: (Any?) -> Unit
) {
    OutlinedTextField(
        value = value?.toString() ?: "",
        onValueChange = onValueChange,
        label = { Text(buildLabel(field)) },
        placeholder = { Text("YYYY-MM-DD") },
        isError = hasError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}

// Time Field (simplified - using text input)
@Composable
private fun TimeFieldComponent(
    field: FormField,
    value: Any?,
    hasError: Boolean,
    onValueChange: (Any?) -> Unit
) {
    OutlinedTextField(
        value = value?.toString() ?: "",
        onValueChange = onValueChange,
        label = { Text(buildLabel(field)) },
        placeholder = { Text("HH:MM") },
        isError = hasError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}

// TextArea (multi-line)
@Composable
private fun TextAreaComponent(
    field: FormField,
    value: Any?,
    hasError: Boolean,
    onValueChange: (Any?) -> Unit
) {
    OutlinedTextField(
        value = value?.toString() ?: "",
        onValueChange = onValueChange,
        label = { Text(buildLabel(field)) },
        isError = hasError,
        minLines = 3,
        maxLines = 6,
        modifier = Modifier.fillMaxWidth()
    )
}

// MultiSelect (simplified - shows selected items as chips)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MultiSelectComponent(
    field: FormField,
    value: Any?,
    hasError: Boolean,
    onValueChange: (Any?) -> Unit
) {
    val options = field.options ?: emptyList()
    val selectedItems = (value as? List<*>)?.map { it.toString() } ?: emptyList()
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = if (selectedItems.isEmpty()) "" else "${selectedItems.size} selected",
            onValueChange = {},
            readOnly = true,
            label = { Text(buildLabel(field)) },
            isError = hasError,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                val isSelected = option in selectedItems
                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null
                            )
                            Text(option)
                        }
                    },
                    onClick = {
                        val newSelection = if (isSelected) {
                            selectedItems - option
                        } else {
                            selectedItems + option
                        }
                        onValueChange(newSelection)
                    }
                )
            }
        }
    }
}

private fun buildLabel(field: FormField): String {
    return if (field.isRequired) "${field.title} *" else field.title
}
