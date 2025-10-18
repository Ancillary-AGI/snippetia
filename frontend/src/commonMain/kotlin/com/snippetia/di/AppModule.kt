package com.snippetia.di

import com.snippetia.data.api.*
import com.snippetia.data.network.HttpClientFactory
import com.snippetia.data.repository.*
import com.snippetia.data.storage.*
import com.snippetia.domain.usecase.*
import com.snippetia.presentation.viewmodel.*
import org.koin.dsl.module

/**
 * Application Dependency Injection Module
 * 
 * This module configures all dependencies for the Snippetia application using Koin.
 * It follows clean architecture principles with clear separation of concerns:
 * 
 * Architecture Layers:
 * - Data Layer: API services, repositories, local storage
 * - Domain Layer: Use cases, business logic
 * - Presentation Layer: ViewModels, screen models
 * 
 * Features:
 * - Singleton pattern for shared resources (HTTP client, storage)
 * - Factory pattern for use cases and view models
 * - Automatic dependency resolution and injection
 * - Platform-specific implementations where needed
 * 
 * Dependencies:
 * - Network layer with Ktor HTTP client
 * - Local storage with platform-specific implementations
 * - Repository pattern for data access abstraction
 * - Use case pattern for business logic encapsulation
 * - MVVM pattern with Compose integration
 * 
 * @author Snippetia Team
 * @since 1.0.0
 */
val appModule = module {
    // ===== DATA LAYER =====
    
    // Local Storage - Platform-specific secure token storage
    single<TokenStorage> { TokenStorageImpl() }
    
    // Network Layer - HTTP client with authentication and logging
    single { 
        HttpClientFactory.create { 
            runBlocking { get<TokenStorage>().getToken() } // Automatic token injection
        } 
    }
    single { ApiClient(get()) } // Centralized API client with base configuration
    
    // API Services - RESTful service implementations
    single<SnippetApiService> { SnippetApiServiceImpl(get()) }
    single<AuthApiService> { AuthApiServiceImpl(get()) }
    
    // Repository Layer - Data access abstraction with caching and offline support
    single<SnippetRepository> { SnippetRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    
    // ===== DOMAIN LAYER =====
    
    // Snippet Use Cases - Business logic for code snippet operations
    factory { GetSnippetsUseCase(get()) }           // Retrieve paginated snippets
    factory { GetFeaturedSnippetsUseCase(get()) }   // Get curated featured content
    factory { GetTrendingSnippetsUseCase(get()) }   // Get trending/popular snippets
    factory { SearchSnippetsUseCase(get()) }        // Full-text search functionality
    factory { GetSnippetUseCase(get()) }            // Get single snippet details
    factory { LikeSnippetUseCase(get()) }           // Toggle like/unlike
    factory { ForkSnippetUseCase(get()) }           // Create snippet fork
    factory { CreateSnippetUseCase(get()) }         // Create new snippet
    factory { UpdateSnippetUseCase(get()) }         // Update existing snippet
    factory { DeleteSnippetUseCase(get()) }         // Delete snippet
    
    // Authentication Use Cases - User authentication and profile management
    factory { LoginUseCase(get()) }                 // User login with credentials
    factory { RegisterUseCase(get()) }              // New user registration
    factory { ForgotPasswordUseCase(get()) }        // Password reset request
    factory { LogoutUseCase(get()) }                // User logout and cleanup
    factory { GetCurrentUserUseCase(get()) }        // Get authenticated user info
    factory { RefreshTokenUseCase(get()) }          // Refresh authentication token
    factory { VerifyEmailUseCase(get()) }           // Email verification
    factory { ResetPasswordUseCase(get()) }         // Password reset with token
    factory { ChangePasswordUseCase(get()) }        // Change user password
    factory { UpdateProfileUseCase(get()) }         // Update user profile
    
    // ===== PRESENTATION LAYER =====
    
    // Screen Models / ViewModels - UI state management with Compose integration
    factory { HomeScreenModel(get(), get(), get(), get(), get(), get()) }      // Home screen with snippets feed
    factory { CreateSnippetScreenModel(get(), get()) }                         // Snippet creation and editing
    factory { SnippetDetailScreenModel(get(), get(), get()) }                  // Detailed snippet view
    factory { AuthScreenModel(get(), get(), get()) }                           // Authentication screens
}

/**
 * Initialize Koin dependency injection framework
 * 
 * This function sets up the DI container with all application modules.
 * Should be called once during application startup, typically in the
 * main function or application class.
 * 
 * Features:
 * - Automatic dependency resolution
 * - Circular dependency detection
 * - Module validation in debug builds
 * - Performance optimizations for production
 */
fun initKoin() {
    org.koin.core.context.startKoin {
        modules(appModule) // Load all dependency modules
    }
}