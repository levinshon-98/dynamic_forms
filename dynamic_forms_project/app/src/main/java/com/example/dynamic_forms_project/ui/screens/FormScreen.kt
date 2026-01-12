package com.example.dynamic_forms_project.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dynamic_forms_project.data.FormField
import com.example.dynamic_forms_project.ui.FormUiState
import com.example.dynamic_forms_project.ui.FormViewModel
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
    viewModel: FormViewModel,
    onFieldChange: (String, Any?) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    getCurrentPayload: () -> String,
    getSchemaJson: () -> String,
    isFormValid: () -> Boolean
) {
    var showSchemaDialog by remember { mutableStateOf(false) }
    var showPayloadDialog by remember { mutableStateOf(false) }
    var showUiSchemaDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("הגדרת חיישן") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "חזור")
                    }
                },
                actions = {
                    // UI Schema button - only if uiSchema exists
                    if ((uiState as? FormUiState.FormReady)?.uiSchema != null) {
                        IconButton(onClick = { showUiSchemaDialog = true }) {
                            Icon(Icons.Default.ViewModule, contentDescription = "תצוגת UI Schema")
                        }
                    }
                    IconButton(onClick = { showPayloadDialog = true }) {
                        Icon(Icons.Default.DataObject, contentDescription = "תצוגת נתונים")
                    }
                    IconButton(onClick = { showSchemaDialog = true }) {
                        Icon(Icons.Default.Description, contentDescription = "תצוגת Schema")
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
                FormContentWithLayout(
                    state = uiState,
                    viewModel = viewModel,
                    onFieldChange = onFieldChange,
                    onSubmit = onSubmit,
                    isLoading = false,
                    isFormValid = isFormValid(),
                    modifier = Modifier.padding(padding)
                )
            }
            
            is FormUiState.Submitting -> {
                FormContentWithLayout(
                    state = uiState.let {
                        FormUiState.FormReady(
                            fields = it.fields,
                            formData = it.formData,
                            errors = emptyMap(),
                            schemaJson = "",
                            uiSchema = null
                        )
                    },
                    viewModel = viewModel,
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
                            Text("חזור")
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
    
    // UI Schema Dialog
    if (showUiSchemaDialog) {
        val uiSchemaJson = (uiState as? FormUiState.FormReady)?.uiSchema?.let {
            com.fasterxml.jackson.databind.ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(it)
        } ?: "{}"
        
        AlertDialog(
            onDismissRequest = { showUiSchemaDialog = false },
            title = { Text("UI Schema") },
            text = {
                JsonViewer(
                    json = uiSchemaJson,
                    modifier = Modifier.heightIn(max = 400.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = { showUiSchemaDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun FormContentWithLayout(
    state: FormUiState.FormReady,
    viewModel: FormViewModel,
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
        com.example.dynamic_forms_project.ui.components.FormContent(
            fields = state.fields,
            formData = state.formData,
            errors = state.errors,
            uiSchema = state.uiSchema,
            isFieldVisible = { viewModel.isFieldVisible(it) },
            isFieldEnabled = { viewModel.isFieldEnabled(it) },
            onValueChange = onFieldChange
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LoadingButton(
            text = "✓ Submit",
            onClick = onSubmit,
            isLoading = isLoading,
            enabled = isFormValid
        )
    }
}
