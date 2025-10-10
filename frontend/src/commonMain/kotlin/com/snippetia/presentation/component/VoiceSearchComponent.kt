package com.snippetia.presentation.component

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Advanced Speech-to-Text Search Component with AI-powered voice recognition
 */

@Composable
fun VoiceSearchComponent(
    onSearchQuery: (String) -> Unit,
    onVoiceCommandDetected: (VoiceCommand) -> Unit,
    modifier: Modifier = Modifier
) {
    var isListening by remember { mutableStateOf(false) }
    var transcribedText by remember { mutableStateOf("") }
    var voiceSearchState by remember { mutableStateOf(VoiceSearchState.IDLE) }
    var audioLevel by remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(isListening) {
        if (isListening) {
            voiceSearchState = VoiceSearchState.LISTENING
            
            // Simulate audio level animation
            while (isListening) {
                audioLevel = (0.1f..1f).random()
                delay(100)
            }
        } else {
            audioLevel = 0f
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Voice Search Button with Animation
            VoiceSearchButton(
                isListening = isListening,
                audioLevel = audioLevel,
                state = voiceSearchState,
                onClick = {
                    if (isListening) {
                        // Stop listening
                        isListening = false
                        voiceSearchState = VoiceSearchState.PROCESSING
                        
                        coroutineScope.launch {
                            // Process the transcribed text
                            delay(1000) // Simulate processing
                            
                            if (transcribedText.isNotBlank()) {
                                // Detect voice commands
                                val command = detectVoiceCommand(transcribedText)
                                if (command != null) {
                                    onVoiceCommandDetected(command)
                                } else {
                                    onSearchQuery(transcribedText)
                                }
                            }
                            
                            voiceSearchState = VoiceSearchState.IDLE
                            transcribedText = ""
                        }
                    } else {
                        // Start listening
                        isListening = true
                        transcribedText = ""
                        
                        // Simulate voice recognition
                        coroutineScope.launch {
                            delay(2000)
                            if (isListening) {
                                transcribedText = "search for React hooks"
                            }
                        }
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Status Text
            AnimatedContent(
                targetState = voiceSearchState,
                transitionSpec = {
                    slideInVertically { it } + fadeIn() with
                    slideOutVertically { -it } + fadeOut()
                },
                label = "status_text"
            ) { state ->
                Text(
                    text = when (state) {
                        VoiceSearchState.IDLE -> "Tap to search with voice"
                        VoiceSearchState.LISTENING -> "Listening... Speak now"
                        VoiceSearchState.PROCESSING -> "Processing your request..."
                        VoiceSearchState.ERROR -> "Error occurred. Try again."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (state) {
                        VoiceSearchState.ERROR -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            // Transcribed Text Preview
            AnimatedVisibility(
                visible = transcribedText.isNotBlank(),
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = transcribedText,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VoiceSearchButton(
    isListening: Boolean,
    audioLevel: Float,
    state: VoiceSearchState,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isListening) 1f + (audioLevel * 0.3f) else 1f,
        animationSpec = tween(100),
        label = "scale"
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    
    Box(
        contentAlignment = Alignment.Center
    ) {
        // Pulse rings when listening
        if (isListening) {
            repeat(3) { index ->
                val delay = index * 200
                val ringScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, delayMillis = delay, easing = LinearOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "ring_scale_$index"
                )
                
                val ringAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.6f,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, delayMillis = delay, easing = LinearOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "ring_alpha_$index"
                )
                
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(ringScale)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = ringAlpha),
                            shape = CircleShape
                        )
                )
            }
        }
        
        // Main button
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier
                .size(80.dp)
                .scale(scale),
            containerColor = when (state) {
                VoiceSearchState.LISTENING -> MaterialTheme.colorScheme.error
                VoiceSearchState.PROCESSING -> MaterialTheme.colorScheme.tertiary
                VoiceSearchState.ERROR -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.primary
            }
        ) {
            when (state) {
                VoiceSearchState.PROCESSING -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
                VoiceSearchState.ERROR -> {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = "Error",
                        modifier = Modifier.size(32.dp)
                    )
                }
                else -> {
                    Icon(
                        if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isListening) "Stop listening" else "Start voice search",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

// Voice command detection
fun detectVoiceCommand(text: String): VoiceCommand? {
    val lowercaseText = text.lowercase()
    
    return when {
        lowercaseText.contains("search") || lowercaseText.contains("find") -> {
            val query = extractSearchQuery(text)
            VoiceCommand.Search(query)
        }
        lowercaseText.contains("create") && lowercaseText.contains("snippet") -> {
            VoiceCommand.CreateSnippet
        }
        lowercaseText.contains("debug") -> {
            VoiceCommand.Debug
        }
        lowercaseText.contains("explain") -> {
            VoiceCommand.Explain
        }
        lowercaseText.contains("run") || lowercaseText.contains("execute") -> {
            VoiceCommand.RunCode
        }
        lowercaseText.contains("repository") || lowercaseText.contains("repo") -> {
            VoiceCommand.ShowRepositories
        }
        lowercaseText.contains("help") -> {
            VoiceCommand.Help
        }
        else -> null
    }
}

private fun extractSearchQuery(text: String): String {
    // Extract the actual search query from voice command
    val patterns = listOf(
        "search for (.*)",
        "find (.*)",
        "look for (.*)",
        "show me (.*)"
    )
    
    for (pattern in patterns) {
        val regex = pattern.toRegex(RegexOption.IGNORE_CASE)
        val match = regex.find(text)
        if (match != null && match.groupValues.size > 1) {
            return match.groupValues[1].trim()
        }
    }
    
    return text // Return original text if no pattern matches
}

// Data classes and enums
enum class VoiceSearchState {
    IDLE, LISTENING, PROCESSING, ERROR
}

sealed class VoiceCommand {
    data class Search(val query: String) : VoiceCommand()
    object CreateSnippet : VoiceCommand()
    object Debug : VoiceCommand()
    object Explain : VoiceCommand()
    object RunCode : VoiceCommand()
    object ShowRepositories : VoiceCommand()
    object Help : VoiceCommand()
}