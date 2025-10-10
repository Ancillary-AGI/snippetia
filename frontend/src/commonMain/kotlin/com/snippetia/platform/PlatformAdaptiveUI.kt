package com.snippetia.platform

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min
import kotlin.math.max

/**
 * Platform-Adaptive UI System
 * Supports all form factors: Phone, Tablet, Desktop, Watch, TV, XR Headsets
 */

enum class FormFactor {
    PHONE, TABLET, DESKTOP, WATCH, TV, XR_HEADSET, FOLDABLE, CAR_DISPLAY
}

enum class Orientation {
    PORTRAIT, LANDSCAPE
}

data class ScreenConfiguration(
    val formFactor: FormFactor,
    val orientation: Orientation,
    val widthDp: Dp,
    val heightDp: Dp,
    val density: Float,
    val isHighRefreshRate: Boolean = false,
    val supportsHDR: Boolean = false,
    val hasPhysicalKeyboard: Boolean = false,
    val hasStylus: Boolean = false,
    val isVR: Boolean = false,
    val is3DCapable: Boolean = false
)

@Composable
fun rememberScreenConfiguration(): ScreenConfiguration {
    val density = LocalDensity.current
    
    // Get screen dimensions
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    
    // Determine form factor based on screen size and characteristics
    val formFactor = when {
        screenWidth < 600.dp -> FormFactor.PHONE
        screenWidth < 840.dp -> FormFactor.TABLET
        screenWidth < 1200.dp -> FormFactor.DESKTOP
        screenWidth > 2000.dp -> FormFactor.TV
        screenHeight < 300.dp && screenWidth < 300.dp -> FormFactor.WATCH
        else -> FormFactor.DESKTOP
    }
    
    val orientation = if (screenWidth > screenHeight) Orientation.LANDSCAPE else Orientation.PORTRAIT
    
    return ScreenConfiguration(
        formFactor = formFactor,
        orientation = orientation,
        widthDp = screenWidth,
        heightDp = screenHeight,
        density = density.density,
        isHighRefreshRate = true, // Assume modern devices
        supportsHDR = formFactor in listOf(FormFactor.TV, FormFactor.DESKTOP, FormFactor.XR_HEADSET)
    )
}

@Composable
fun AdaptiveLayout(
    modifier: Modifier = Modifier,
    content: @Composable (ScreenConfiguration) -> Unit
) {
    val screenConfig = rememberScreenConfiguration()
    
    Box(modifier = modifier) {
        content(screenConfig)
    }
}

@Composable
fun ResponsiveGrid(
    modifier: Modifier = Modifier,
    minItemWidth: Dp = 300.dp,
    spacing: Dp = 16.dp,
    content: @Composable LazyGridScope.() -> Unit
) {
    val screenConfig = rememberScreenConfiguration()
    
    val columns = when (screenConfig.formFactor) {
        FormFactor.PHONE -> if (screenConfig.orientation == Orientation.PORTRAIT) 1 else 2
        FormFactor.TABLET -> if (screenConfig.orientation == Orientation.PORTRAIT) 2 else 3
        FormFactor.DESKTOP -> {
            val availableWidth = screenConfig.widthDp - (spacing * 2)
            max(1, (availableWidth / (minItemWidth + spacing)).toInt())
        }
        FormFactor.TV -> 4
        FormFactor.WATCH -> 1
        FormFactor.XR_HEADSET -> 3
        FormFactor.FOLDABLE -> if (screenConfig.orientation == Orientation.PORTRAIT) 1 else 2
        FormFactor.CAR_DISPLAY -> 2
    }
    
    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
        columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = PaddingValues(spacing),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalArrangement = Arrangement.spacedBy(spacing),
        content = content
    )
}

@Composable
fun AdaptiveNavigation(
    screenConfig: ScreenConfiguration,
    navigationItems: List<NavigationItem>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (screenConfig.formFactor) {
        FormFactor.PHONE -> {
            // Bottom navigation for phones
            NavigationBar(modifier = modifier) {
                navigationItems.forEach { item ->
                    NavigationBarItem(
                        selected = selectedItem == item.id,
                        onClick = { onItemSelected(item.id) },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
        
        FormFactor.TABLET -> {
            if (screenConfig.orientation == Orientation.LANDSCAPE) {
                // Navigation rail for landscape tablets
                NavigationRail(modifier = modifier) {
                    navigationItems.forEach { item ->
                        NavigationRailItem(
                            selected = selectedItem == item.id,
                            onClick = { onItemSelected(item.id) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            } else {
                // Bottom navigation for portrait tablets
                NavigationBar(modifier = modifier) {
                    navigationItems.forEach { item ->
                        NavigationBarItem(
                            selected = selectedItem == item.id,
                            onClick = { onItemSelected(item.id) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
        
        FormFactor.DESKTOP, FormFactor.TV -> {
            // Navigation drawer for desktop and TV
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet(modifier = Modifier.width(240.dp)) {
                        navigationItems.forEach { item ->
                            NavigationDrawerItem(
                                selected = selectedItem == item.id,
                                onClick = { onItemSelected(item.id) },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    }
                },
                modifier = modifier
            ) {
                // Content area
            }
        }
        
        FormFactor.WATCH -> {
            // Circular navigation for watches
            WatchNavigationCircle(
                items = navigationItems,
                selectedItem = selectedItem,
                onItemSelected = onItemSelected,
                modifier = modifier
            )
        }
        
        FormFactor.XR_HEADSET -> {
            // 3D spatial navigation for XR
            XRSpatialNavigation(
                items = navigationItems,
                selectedItem = selectedItem,
                onItemSelected = onItemSelected,
                modifier = modifier
            )
        }
        
        FormFactor.FOLDABLE -> {
            // Adaptive navigation for foldable devices
            if (screenConfig.widthDp > 840.dp) {
                // Dual pane navigation
                DualPaneNavigation(
                    items = navigationItems,
                    selectedItem = selectedItem,
                    onItemSelected = onItemSelected,
                    modifier = modifier
                )
            } else {
                // Standard bottom navigation
                NavigationBar(modifier = modifier) {
                    navigationItems.forEach { item ->
                        NavigationBarItem(
                            selected = selectedItem == item.id,
                            onClick = { onItemSelected(item.id) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
        
        FormFactor.CAR_DISPLAY -> {
            // Large touch targets for car displays
            CarDisplayNavigation(
                items = navigationItems,
                selectedItem = selectedItem,
                onItemSelected = onItemSelected,
                modifier = modifier
            )
        }
    }
}

@Composable
fun AdaptiveTypography(
    screenConfig: ScreenConfiguration
): Typography {
    val baseTypography = MaterialTheme.typography
    
    return when (screenConfig.formFactor) {
        FormFactor.PHONE -> baseTypography
        
        FormFactor.TABLET -> baseTypography.copy(
            headlineLarge = baseTypography.headlineLarge.copy(fontSize = baseTypography.headlineLarge.fontSize * 1.1f),
            headlineMedium = baseTypography.headlineMedium.copy(fontSize = baseTypography.headlineMedium.fontSize * 1.1f),
            bodyLarge = baseTypography.bodyLarge.copy(fontSize = baseTypography.bodyLarge.fontSize * 1.05f)
        )
        
        FormFactor.DESKTOP -> baseTypography.copy(
            headlineLarge = baseTypography.headlineLarge.copy(fontSize = baseTypography.headlineLarge.fontSize * 1.2f),
            headlineMedium = baseTypography.headlineMedium.copy(fontSize = baseTypography.headlineMedium.fontSize * 1.15f),
            bodyLarge = baseTypography.bodyLarge.copy(fontSize = baseTypography.bodyLarge.fontSize * 1.1f)
        )
        
        FormFactor.TV -> baseTypography.copy(
            headlineLarge = baseTypography.headlineLarge.copy(fontSize = baseTypography.headlineLarge.fontSize * 1.5f),
            headlineMedium = baseTypography.headlineMedium.copy(fontSize = baseTypography.headlineMedium.fontSize * 1.3f),
            bodyLarge = baseTypography.bodyLarge.copy(fontSize = baseTypography.bodyLarge.fontSize * 1.2f)
        )
        
        FormFactor.WATCH -> baseTypography.copy(
            headlineLarge = baseTypography.headlineMedium,
            headlineMedium = baseTypography.titleLarge,
            bodyLarge = baseTypography.bodyMedium.copy(fontSize = baseTypography.bodyMedium.fontSize * 0.9f)
        )
        
        FormFactor.XR_HEADSET -> baseTypography.copy(
            headlineLarge = baseTypography.headlineLarge.copy(fontSize = baseTypography.headlineLarge.fontSize * 1.3f),
            headlineMedium = baseTypography.headlineMedium.copy(fontSize = baseTypography.headlineMedium.fontSize * 1.2f),
            bodyLarge = baseTypography.bodyLarge.copy(fontSize = baseTypography.bodyLarge.fontSize * 1.15f)
        )
        
        FormFactor.FOLDABLE -> baseTypography.copy(
            headlineLarge = baseTypography.headlineLarge.copy(fontSize = baseTypography.headlineLarge.fontSize * 1.1f),
            bodyLarge = baseTypography.bodyLarge.copy(fontSize = baseTypography.bodyLarge.fontSize * 1.05f)
        )
        
        FormFactor.CAR_DISPLAY -> baseTypography.copy(
            headlineLarge = baseTypography.headlineLarge.copy(fontSize = baseTypography.headlineLarge.fontSize * 1.4f),
            headlineMedium = baseTypography.headlineMedium.copy(fontSize = baseTypography.headlineMedium.fontSize * 1.25f),
            bodyLarge = baseTypography.bodyLarge.copy(fontSize = baseTypography.bodyLarge.fontSize * 1.15f)
        )
    }
}

@Composable
fun AdaptiveSpacing(
    screenConfig: ScreenConfiguration
): PaddingValues {
    return when (screenConfig.formFactor) {
        FormFactor.PHONE -> PaddingValues(16.dp)
        FormFactor.TABLET -> PaddingValues(24.dp)
        FormFactor.DESKTOP -> PaddingValues(32.dp)
        FormFactor.TV -> PaddingValues(48.dp)
        FormFactor.WATCH -> PaddingValues(8.dp)
        FormFactor.XR_HEADSET -> PaddingValues(40.dp)
        FormFactor.FOLDABLE -> PaddingValues(20.dp)
        FormFactor.CAR_DISPLAY -> PaddingValues(36.dp)
    }
}

// Platform-specific navigation components
@Composable
private fun WatchNavigationCircle(
    items: List<NavigationItem>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Circular navigation optimized for watch displays
    Box(modifier = modifier.fillMaxSize()) {
        // Implementation for circular watch navigation
        // This would use Canvas to draw circular menu
    }
}

@Composable
private fun XRSpatialNavigation(
    items: List<NavigationItem>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 3D spatial navigation for XR headsets
    Box(modifier = modifier.fillMaxSize()) {
        // Implementation for 3D spatial navigation
        // This would use 3D transforms and depth
    }
}

@Composable
private fun DualPaneNavigation(
    items: List<NavigationItem>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Dual pane navigation for foldable devices
    Row(modifier = modifier.fillMaxWidth()) {
        // Left pane navigation
        NavigationRail(modifier = Modifier.weight(0.3f)) {
            items.forEach { item ->
                NavigationRailItem(
                    selected = selectedItem == item.id,
                    onClick = { onItemSelected(item.id) },
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) }
                )
            }
        }
        
        // Right pane content area
        Box(modifier = Modifier.weight(0.7f)) {
            // Content goes here
        }
    }
}

@Composable
private fun CarDisplayNavigation(
    items: List<NavigationItem>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Large touch targets optimized for car displays
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { item ->
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .padding(8.dp),
                onClick = { onItemSelected(item.id) },
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedItem == item.id) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        item.label,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

// Data classes
data class NavigationItem(
    val id: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

// Adaptive theme provider
@Composable
fun AdaptiveTheme(
    screenConfig: ScreenConfiguration,
    content: @Composable () -> Unit
) {
    val adaptiveTypography = AdaptiveTypography(screenConfig)
    
    // Adaptive color scheme based on form factor
    val adaptiveColorScheme = when (screenConfig.formFactor) {
        FormFactor.TV -> {
            // High contrast colors for TV viewing distance
            if (screenConfig.supportsHDR) {
                // HDR-optimized colors
                dynamicDarkColorScheme(androidx.compose.ui.platform.LocalContext.current)
            } else {
                darkColorScheme()
            }
        }
        FormFactor.WATCH -> {
            // Always dark theme for watches to save battery
            darkColorScheme()
        }
        FormFactor.XR_HEADSET -> {
            // Semi-transparent theme for XR overlay
            darkColorScheme().copy(
                surface = Color.Black.copy(alpha = 0.8f),
                background = Color.Transparent
            )
        }
        FormFactor.CAR_DISPLAY -> {
            // High contrast theme for car displays
            if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
        }
        else -> {
            // Standard adaptive theme
            if (isSystemInDarkTheme()) {
                dynamicDarkColorScheme(androidx.compose.ui.platform.LocalContext.current)
            } else {
                dynamicLightColorScheme(androidx.compose.ui.platform.LocalContext.current)
            }
        }
    }
    
    MaterialTheme(
        colorScheme = adaptiveColorScheme,
        typography = adaptiveTypography,
        content = content
    )
}

// Gesture handling for different form factors
@Composable
fun AdaptiveGestureHandler(
    screenConfig: ScreenConfiguration,
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
    onSwipeUp: () -> Unit = {},
    onSwipeDown: () -> Unit = {},
    onPinchZoom: (Float) -> Unit = {},
    onRotate: (Float) -> Unit = {},
    content: @Composable () -> Unit
) {
    when (screenConfig.formFactor) {
        FormFactor.PHONE, FormFactor.TABLET -> {
            // Standard touch gestures
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            when {
                                change.x > 50 -> onSwipeRight()
                                change.x < -50 -> onSwipeLeft()
                                change.y > 50 -> onSwipeDown()
                                change.y < -50 -> onSwipeUp()
                            }
                        }
                    }
            ) {
                content()
            }
        }
        
        FormFactor.WATCH -> {
            // Watch-specific gestures (crown rotation, edge swipes)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        // Implement watch-specific gesture detection
                    }
            ) {
                content()
            }
        }
        
        FormFactor.XR_HEADSET -> {
            // 3D spatial gestures and eye tracking
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        // Implement XR-specific gesture detection
                    }
            ) {
                content()
            }
        }
        
        else -> {
            // Standard content without special gesture handling
            content()
        }
    }
}