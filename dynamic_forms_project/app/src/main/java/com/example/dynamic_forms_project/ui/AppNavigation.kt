package com.example.dynamic_forms_project.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dynamic_forms_project.ui.screens.FormScreen
import com.example.dynamic_forms_project.ui.screens.SuccessScreen
import com.example.dynamic_forms_project.ui.screens.WelcomeScreen

/**
 * Main navigation graph
 */
@Composable
fun AppNavigation(viewModel: FormViewModel = viewModel()) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val schemas by viewModel.schemas.collectAsState()
    val schemasLoading by viewModel.schemasLoading.collectAsState()
    
    // Store success payload to preserve it during navigation
    var successPayload by remember { mutableStateOf("") }
    
    // Handle navigation to success screen
    LaunchedEffect(uiState) {
        if (uiState is FormUiState.Success) {
            successPayload = (uiState as FormUiState.Success).payload
            navController.navigate("success") {
                popUpTo("form") { inclusive = true }
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen(
                schemas = schemas,
                isLoading = schemasLoading,
                onSchemaSelected = { schemaName ->
                    viewModel.loadSchemaByName(schemaName)
                    navController.navigate("form")
                },
                onLoadForm = {
                    viewModel.loadSchema()
                    navController.navigate("form")
                },
                onRefresh = {
                    viewModel.loadAllSchemas()
                }
            )
        }
        
        composable("form") {
            FormScreen(
                uiState = uiState,
                viewModel = viewModel,
                onFieldChange = viewModel::updateField,
                onSubmit = viewModel::submit,
                onBack = {
                    viewModel.reset()
                    navController.popBackStack()
                },
                getCurrentPayload = viewModel::getCurrentPayload,
                getSchemaJson = viewModel::getSchemaJson,
                isFormValid = viewModel::isFormValid
            )
        }
        
        composable("success") {
            SuccessScreen(
                payload = successPayload,
                onSubmitAnother = {
                    viewModel.reset()
                    successPayload = ""
                    navController.navigate("welcome") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }
    }
}
