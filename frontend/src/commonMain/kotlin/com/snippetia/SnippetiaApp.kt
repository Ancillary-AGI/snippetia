package com.snippetia

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.snippetia.presentation.screen.SplashScreen
import com.snippetia.presentation.theme.SnippetiaTheme
import com.snippetia.platform.*
import com.snippetia.performance.PerformanceOptimizedContent
import org.koin.compose.KoinApplication

/**
 * Main Snippetia Application Entry Point
 * 
 * This is the root composable that sets up the entire application architecture.
 * It provides a unified entry point for all supported platforms while maintaining
 * platform-specific optimizations and adaptive UI patterns.
 * 
 * Architecture Features:
 * - Multi-Platform Support: Android, iOS, Desktop, Web, Watch, TV, XR
 * - Adaptive UI: Automatically adjusts to different screen sizes and form factors
 * - Performance Optimizations: Lazy loading, memory management, and efficient rendering
 * - Advanced Animations: Smooth transitions and shader effects
 * - Real-Time Collaboration: WebSocket-based live editing and synchronization
 * - AI Integration: Code analysis, suggestions, and intelligent features
 * 
 * Technical Stack:
 * - Compose Multiplatform for cross-platform UI
 * - Voyager for type-safe navigation
 * - Koin for dependency injection
 * - Material 3 design system
 * - Adaptive layouts for responsive design
 * 
 * @author Snippetia Team
 * @since 1.0.0
 */
@Composable
fun SnippetiaApp() {
    // Initialize dependency injection with Koin
    KoinApplication(
        application = {
            modules(appModule) // Load all application modules (repositories, use cases, view models)
        }
    ) {
        // Get current screen configuration for adaptive UI
        val screenConfig = rememberScreenConfiguration()
        
        // Apply adaptive theme based on screen configuration and user preferences
        AdaptiveTheme(screenConfig = screenConfig) {
            // Wrap content in performance optimizations (lazy loading, memory management)
            PerformanceOptimizedContent {
                // Apply adaptive layout for different screen sizes and orientations
                AdaptiveLayout {
                    // Main application surface with Material 3 theming
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // Initialize navigation with Voyager navigator
                        // Start with SplashScreen and enable slide transitions
                        Navigator(SplashScreen()) { navigator ->
                            SlideTransition(navigator) // Smooth slide animations between screens
                        }
                    }
                }
            }
        }
    }
}