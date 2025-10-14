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
 * Main Snippetia Application
 * 
 * Features:
 * - Multi-Platform Support (Android, iOS, Desktop, Web, Watch, TV, XR)
 * - Adaptive UI for All Form Factors
 * - High-Performance Optimizations
 * - Advanced Animations and Shaders
 * - Real-Time Collaboration
 * - AI-Powered Code Analysis
 */
@Composable
fun SnippetiaApp() {
    KoinApplication(
        application = {
            modules(appModule)
        }
    ) {
        val screenConfig = rememberScreenConfiguration()
        
        AdaptiveTheme(screenConfig = screenConfig) {
            PerformanceOptimizedContent {
                AdaptiveLayout {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Navigator(SplashScreen()) { navigator ->
                            SlideTransition(navigator)
                        }
                    }
                }
            }
        }
    }
}