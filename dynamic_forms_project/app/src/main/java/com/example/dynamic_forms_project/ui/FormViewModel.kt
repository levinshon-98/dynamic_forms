package com.example.dynamic_forms_project.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamic_forms_project.data.FormField
import com.example.dynamic_forms_project.data.SchemaParser
import com.example.dynamic_forms_project.data.SchemaRepository
import com.example.dynamic_forms_project.data.SchemaMetadata
import com.example.dynamic_forms_project.data.UiSchema
import com.example.dynamic_forms_project.data.Condition
import com.example.dynamic_forms_project.data.ConditionOperator
import com.example.dynamic_forms_project.data.RuleEffect
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
    
    private val _schemas = MutableStateFlow<List<SchemaMetadata>>(emptyList())
    val schemas: StateFlow<List<SchemaMetadata>> = _schemas.asStateFlow()
    
    private val _schemasLoading = MutableStateFlow(false)
    val schemasLoading: StateFlow<Boolean> = _schemasLoading.asStateFlow()
    
    init {
        loadAllSchemas()
    }
    
    private fun loadAllSchemas() {
        viewModelScope.launch {
            _schemasLoading.value = true
            repository.getAllSchemas()
                .onSuccess { schemasList ->
                    _schemas.value = schemasList
                }
                .onFailure {
                    _schemas.value = listOf(
                        SchemaMetadata(
                            id = "fallback",
                            name = "Sensor Configuration (Local)",
                            schema = emptyMap(),
                            createdAt = "",
                            updatedAt = ""
                        )
                    )
                }
            _schemasLoading.value = false
        }
    }
    
    fun loadSchemaByName(name: String) {
        viewModelScope.launch {
            _uiState.value = FormUiState.Loading
            
            repository.loadSchemaByName(name)
                .onSuccess { json ->
                    val fields = parser.parse(json)
                    val uiSchema = parser.parseUiSchema(json)
                    val initialData = fields.associate { 
                        it.name to (it.defaultValue ?: getDefaultForType(it))
                    }
                    
                    _uiState.value = FormUiState.FormReady(
                        fields = fields,
                        formData = initialData,
                        errors = emptyMap(),
                        schemaJson = parser.getRawSchema(),
                        uiSchema = uiSchema
                    )
                }
                .onFailure { error ->
                    _uiState.value = FormUiState.Error(
                        error.message ?: "Failed to load schema"
                    )
                }
        }
    }
    
    fun loadSchema() {
        viewModelScope.launch {
            _uiState.value = FormUiState.Loading
            
            repository.loadSchema()
                .onSuccess { json ->
                    val fields = parser.parse(json)
                    val uiSchema = parser.parseUiSchema(json)
                    val initialData = fields.associate { 
                        it.name to (it.defaultValue ?: getDefaultForType(it))
                    }
                    
                    _uiState.value = FormUiState.FormReady(
                        fields = fields,
                        formData = initialData,
                        errors = emptyMap(),
                        schemaJson = parser.getRawSchema(),
                        uiSchema = uiSchema
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
    
    fun isFieldVisible(fieldName: String): Boolean {
        val currentState = _uiState.value as? FormUiState.FormReady ?: return true
        val rules = currentState.uiSchema?.rules ?: return true
        
        val applicableRules = rules.filter { it.field == fieldName }
        if (applicableRules.isEmpty()) return true
        
        return applicableRules.all { rule ->
            val conditionMet = evaluateCondition(rule.condition, currentState.formData)
            when (rule.effect) {
                RuleEffect.SHOW -> conditionMet
                RuleEffect.HIDE -> !conditionMet
                else -> true
            }
        }
    }
    
    fun isFieldEnabled(fieldName: String): Boolean {
        val currentState = _uiState.value as? FormUiState.FormReady ?: return true
        val rules = currentState.uiSchema?.rules ?: return true
        
        val applicableRules = rules.filter { it.field == fieldName }
        if (applicableRules.isEmpty()) return true
        
        return applicableRules.all { rule ->
            val conditionMet = evaluateCondition(rule.condition, currentState.formData)
            when (rule.effect) {
                RuleEffect.ENABLE -> conditionMet
                RuleEffect.DISABLE -> !conditionMet
                else -> true
            }
        }
    }
    
    private fun evaluateCondition(condition: Condition, formData: Map<String, Any?>): Boolean {
        val fieldValue = formData[condition.field]
        val conditionValue = condition.value
        
        return when (condition.operator) {
            ConditionOperator.EQUALS -> fieldValue == conditionValue
            ConditionOperator.NOT_EQUALS -> fieldValue != conditionValue
            ConditionOperator.CONTAINS -> {
                fieldValue?.toString()?.contains(conditionValue?.toString() ?: "", ignoreCase = true) ?: false
            }
            ConditionOperator.GREATER_THAN -> {
                (fieldValue as? Number)?.toDouble()?.let { it > (conditionValue as? Number)?.toDouble() ?: 0.0 } ?: false
            }
            ConditionOperator.LESS_THAN -> {
                (fieldValue as? Number)?.toDouble()?.let { it < (conditionValue as? Number)?.toDouble() ?: 0.0 } ?: false
            }
            ConditionOperator.IS_EMPTY -> fieldValue == null || fieldValue.toString().isBlank()
            ConditionOperator.NOT_EMPTY -> fieldValue != null && fieldValue.toString().isNotBlank()
        }
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
        val schemaJson: String,
        val uiSchema: UiSchema? = null
    ) : FormUiState()
    
    data class Submitting(
        val fields: List<FormField>,
        val formData: Map<String, Any?>
    ) : FormUiState()
    
    data class Success(val payload: String) : FormUiState()
    
    data class Error(val message: String) : FormUiState()
}
