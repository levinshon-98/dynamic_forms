package com.example.dynamic_forms_project.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dynamic_forms_project.data.FormField
import com.example.dynamic_forms_project.ui.FormUiState
import com.example.dynamic_forms_project.ui.components.DynamicField
import com.example.dynamic_forms_project.ui.components.JsonViewer
import com.example.dynamic_forms_project.ui.components.LoadingButton

/**
 * Form screen with dynamic fields and validation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    uiState: FormUiState,
    onFieldChange: (String, Any?) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    getCurrentPayload: () -> String,
    getSchemaJson: () -> String,
    isFormValid: () -> Boolean
) {
    var showSchemaDialog by remember { mutableStateOf(false) }
    var showPayloadDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dynamic Form") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showSchemaDialog = true }) {
                        Icon(Icons.Default.Description, contentDescription = "View Schema")
                    }
                    IconButton(onClick = { showPayloadDialog = true }) {
                        Icon(Icons.Default.DataObject, contentDescription = "View Payload")
                    }
                }
            )
        }
    ) { padding ->
        when (uiState) {
            is FormUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Loading schema...")
                    }
                }
            }
            
            is FormUiState.FormReady -> {
                FormContent(
                    fields = uiState.fields,
                    formData = uiState.formData,
                    errors = uiState.errors,
                    onFieldChange = onFieldChange,
                    onSubmit = onSubmit,
                    isLoading = false,
                    isFormValid = isFormValid(),
                    modifier = Modifier.padding(padding)
                )
            }
            
            is FormUiState.Submitting -> {
                FormContent(
                    fields = uiState.fields,
                    formData = uiState.formData,
                    errors = emptyMap(),
                    onFieldChange = { _, _ -> },
                    onSubmit = { },
                    isLoading = true,
                    isFormValid = true,
                    modifier = Modifier.padding(padding)
                )
            }
            
            is FormUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(uiState.message)
                        Button(onClick = onBack) {
                            Text("Go Back")
                        }
                    }
                }
            }
            
            else -> {}
        }
    }
    
    // Schema Dialog
    if (showSchemaDialog) {
        AlertDialog(
            onDismissRequest = { showSchemaDialog = false },
            title = { Text("JSON Schema") },
            text = {
                JsonViewer(
                    json = getSchemaJson(),
                    modifier = Modifier.heightIn(max = 400.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = { showSchemaDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
    
    // Payload Dialog
    if (showPayloadDialog) {
        AlertDialog(
            onDismissRequest = { showPayloadDialog = false },
            title = { Text("Current Payload") },
            text = {
                JsonViewer(
                    json = getCurrentPayload(),
                    modifier = Modifier.heightIn(max = 400.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = { showPayloadDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun FormContent(
    fields: List<FormField>,
    formData: Map<String, Any?>,
    errors: Map<String, String>,
    onFieldChange: (String, Any?) -> Unit,
    onSubmit: () -> Unit,
    isLoading: Boolean,
    isFormValid: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        fields.forEach { field ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically()
            ) {
                DynamicField(
                    field = field,
                    value = formData[field.name],
                    error = errors[field.name],
                    onValueChange = { onFieldChange(field.name, it) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LoadingButton(
            text = "✓ Submit",
            onClick = onSubmit,
            isLoading = isLoading,
            enabled = isFormValid
        )
    }
}
