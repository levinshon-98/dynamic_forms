package com.example.dynamic_forms_project.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen(
                onLoadForm = {
                    viewModel.loadSchema()
                    navController.navigate("form")
                }
            )
        }
        
        composable("form") {
            // Navigate to success when submission completes
            if (uiState is FormUiState.Success) {
                navController.navigate("success") {
                    popUpTo("form") { inclusive = true }
                }
            }
            
            FormScreen(
                uiState = uiState,
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
            val payload = (uiState as? FormUiState.Success)?.payload ?: ""
            
            SuccessScreen(
                payload = payload,
                onSubmitAnother = {
                    viewModel.reset()
                    navController.navigate("welcome") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }
    }
}
