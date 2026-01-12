package com.example.dynamic_forms_project.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.example.dynamic_forms_project.ui.components.JsonViewer
import com.example.dynamic_forms_project.ui.theme.SuccessColor
import kotlinx.coroutines.delay

/**
 * Success screen showing submitted payload
 */
@Composable
fun SuccessScreen(
    payload: String,
    onSubmitAnother: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }
    
    // Animation for checkmark
    val scale by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // Success checkmark with bounce animation
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = SuccessColor,
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(500, delayMillis = 300))
        ) {
            Text(
                text = "הגדרת חיישן הושלמה!",
                style = MaterialTheme.typography.headlineMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(500, delayMillis = 500))
        ) {
            Text(
                text = "הנתונים נשלחו בהצלחה",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Payload card
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(500, delayMillis = 700)) +
                    slideInVertically(
                        initialOffsetY = { 50 },
                        animationSpec = tween(500, delayMillis = 700)
                    )
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "נתונים שנשלחו",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    JsonViewer(json = payload)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Submit another button
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(500, delayMillis = 900))
        ) {
            OutlinedButton(
                onClick = onSubmitAnother,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("הגדר חיישן נוסף")
            }
        }
    }
}
