package com.example.dynamic_forms_project.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dynamic_forms_project.R
import kotlinx.coroutines.delay

/**
 * Welcome screen with Tondo logo and load button
 */
@Composable
fun WelcomeScreen(
    onLoadForm: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo with fade-in animation
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(800))
        ) {
            Image(
                painter = painterResource(id = R.drawable.tondo_logo),
                contentDescription = "Tondo Logo",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 32.dp)
            )
        }
        
        // Title
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(800, delayMillis = 200))
        ) {
            Text(
                text = "Dynamic Form Builder",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Subtitle
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(800, delayMillis = 400))
        ) {
            Text(
                text = "Load forms dynamically from JSON Schema",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Load button with slide-up animation
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { 100 },
                animationSpec = tween(600, delayMillis = 600)
            ) + fadeIn(animationSpec = tween(600, delayMillis = 600))
        ) {
            Button(
                onClick = onLoadForm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("🔄 Load Form")
            }
        }
    }
}
