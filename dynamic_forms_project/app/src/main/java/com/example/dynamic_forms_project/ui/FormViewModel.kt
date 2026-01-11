package com.example.dynamic_forms_project.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamic_forms_project.data.FormField
import com.example.dynamic_forms_project.data.SchemaParser
import com.example.dynamic_forms_project.data.SchemaRepository
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel managing form state and validation
 */
class FormViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = SchemaRepository(application)
    private val parser = SchemaParser()
    private val mapper = ObjectMapper()
    
    private val _uiState = MutableStateFlow<FormUiState>(FormUiState.Initial)
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()
    
    fun loadSchema() {
        viewModelScope.launch {
            _uiState.value = FormUiState.Loading
            
            repository.loadSchema()
                .onSuccess { json ->
                    val fields = parser.parse(json)
                    val initialData = fields.associate { 
                        it.name to (it.defaultValue ?: getDefaultForType(it))
                    }
                    
                    _uiState.value = FormUiState.FormReady(
                        fields = fields,
                        formData = initialData,
                        errors = emptyMap(),
                        schemaJson = parser.getRawSchema()
                    )
                }
                .onFailure { error ->
                    _uiState.value = FormUiState.Error(
                        error.message ?: "Failed to load schema"
                    )
                }
        }
    }
    
    private fun getDefaultForType(field: FormField): Any? {
        return when (field.type) {
            com.example.dynamic_forms_project.data.FieldType.BOOLEAN -> false
            com.example.dynamic_forms_project.data.FieldType.NUMBER -> null
            com.example.dynamic_forms_project.data.FieldType.DROPDOWN -> field.options?.firstOrNull()
            else -> ""
        }
    }
    
    fun updateField(fieldName: String, value: Any?) {
        val currentState = _uiState.value as? FormUiState.FormReady ?: return
        
        val newData = currentState.formData.toMutableMap()
        newData[fieldName] = value
        
        // Validate
        val filteredData = newData.filterValues { 
            it != null && it.toString().isNotBlank() 
        }
        val errors = parser.validate(filteredData)
        
        _uiState.value = currentState.copy(
            formData = newData,
            errors = errors
        )
    }
    
    fun submit() {
        val currentState = _uiState.value as? FormUiState.FormReady ?: return
        
        // Final validation
        val filteredData = currentState.formData.filterValues { 
            it != null && it.toString().isNotBlank() 
        }
        val errors = parser.validate(filteredData)
        
        if (errors.isNotEmpty()) {
            _uiState.value = currentState.copy(errors = errors)
            return
        }
        
        // Simulate submission
        viewModelScope.launch {
            _uiState.value = FormUiState.Submitting(currentState.fields, currentState.formData)
            
            delay(2000) // Simulate network delay
            
            val payload = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(filteredData)
            
            _uiState.value = FormUiState.Success(payload)
        }
    }
    
    fun getCurrentPayload(): String {
        val currentState = _uiState.value
        val data = when (currentState) {
            is FormUiState.FormReady -> currentState.formData
            is FormUiState.Submitting -> currentState.formData
            else -> emptyMap()
        }
        
        val filtered = data.filterValues { it != null && it.toString().isNotBlank() }
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(filtered)
    }
    
    fun getSchemaJson(): String {
        return (uiState.value as? FormUiState.FormReady)?.schemaJson ?: ""
    }
    
    fun reset() {
        _uiState.value = FormUiState.Initial
    }
    
    fun isFormValid(): Boolean {
        val state = _uiState.value as? FormUiState.FormReady ?: return false
        
        // Check required fields
        val hasAllRequired = state.fields
            .filter { it.isRequired }
            .all { field ->
                val value = state.formData[field.name]
                value != null && value.toString().isNotBlank()
            }
        
        return hasAllRequired && state.errors.isEmpty()
    }
}

sealed class FormUiState {
    data object Initial : FormUiState()
    data object Loading : FormUiState()
    
    data class FormReady(
        val fields: List<FormField>,
        val formData: Map<String, Any?>,
        val errors: Map<String, String>,
        val schemaJson: String
    ) : FormUiState()
    
    data class Submitting(
        val fields: List<FormField>,
        val formData: Map<String, Any?>
    ) : FormUiState()
    
    data class Success(val payload: String) : FormUiState()
    
    data class Error(val message: String) : FormUiState()
}
