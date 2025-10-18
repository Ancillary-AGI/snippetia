package com.snippetia.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

/**
 * ApiClient - Centralized HTTP client for API communication
 * 
 * This class provides a unified interface for making HTTP requests to the
 * Snippetia backend API. It handles request/response serialization, error
 * handling, and provides a consistent API across all platforms.
 * 
 * Features:
 * - Type-safe request/response handling with Kotlin serialization
 * - Automatic error handling and response wrapping
 * - Support for all HTTP methods (GET, POST, PUT, DELETE)
 * - Query parameter handling for GET requests
 * - JSON content type handling for request bodies
 * - Comprehensive error reporting with status codes
 * 
 * Error Handling:
 * - Network errors are caught and wrapped in ApiResponse.Error
 * - HTTP error status codes are preserved and reported
 * - Response body is included in error messages when available
 * - Timeout and connection errors are handled gracefully
 * 
 * Usage:
 * ```kotlin
 * val response = apiClient.get<List<Snippet>>("/snippets")
 * response.onSuccess { snippets -> 
 *     // Handle successful response
 * }.onError { code, message ->
 *     // Handle error response
 * }
 * ```
 * 
 * @param httpClient The configured Ktor HTTP client
 * @param baseUrl The base URL for API requests (default: localhost for development)
 * 
 * @author Snippetia Team
 * @since 1.0.0
 */
class ApiClient(
    private val httpClient: HttpClient,
    private val baseUrl: String = "http://localhost:8080/api"
) {
    
    /**
     * Perform a GET request with optional query parameters
     * 
     * @param T The expected response type
     * @param endpoint The API endpoint path (e.g., "/snippets")
     * @param parameters Query parameters to include in the request
     * @return ApiResponse wrapping the result or error
     */
    suspend inline fun <reified T> get(
        endpoint: String,
        parameters: Map<String, Any> = emptyMap()
    ): ApiResponse<T> {
        return try {
            val response = httpClient.get("$baseUrl$endpoint") {
                parameters.forEach { (key, value) ->
                    parameter(key, value.toString())
                }
            }
            
            if (response.status.isSuccess()) {
                ApiResponse.Success(response.body<T>())
            } else {
                ApiResponse.Error(response.status.value, response.bodyAsText())
            }
        } catch (e: Exception) {
            ApiResponse.Error(0, e.message ?: "Unknown error")
        }
    }
    
    suspend inline fun <reified T> post(
        endpoint: String,
        body: Any? = null
    ): ApiResponse<T> {
        return try {
            val response = httpClient.post("$baseUrl$endpoint") {
                contentType(ContentType.Application.Json)
                if (body != null) {
                    setBody(body)
                }
            }
            
            if (response.status.isSuccess()) {
                ApiResponse.Success(response.body<T>())
            } else {
                ApiResponse.Error(response.status.value, response.bodyAsText())
            }
        } catch (e: Exception) {
            ApiResponse.Error(0, e.message ?: "Unknown error")
        }
    }
    
    suspend inline fun <reified T> put(
        endpoint: String,
        body: Any? = null
    ): ApiResponse<T> {
        return try {
            val response = httpClient.put("$baseUrl$endpoint") {
                contentType(ContentType.Application.Json)
                if (body != null) {
                    setBody(body)
                }
            }
            
            if (response.status.isSuccess()) {
                ApiResponse.Success(response.body<T>())
            } else {
                ApiResponse.Error(response.status.value, response.bodyAsText())
            }
        } catch (e: Exception) {
            ApiResponse.Error(0, e.message ?: "Unknown error")
        }
    }
    
    suspend fun delete(endpoint: String): ApiResponse<Unit> {
        return try {
            val response = httpClient.delete("$baseUrl$endpoint")
            
            if (response.status.isSuccess()) {
                ApiResponse.Success(Unit)
            } else {
                ApiResponse.Error(response.status.value, response.bodyAsText())
            }
        } catch (e: Exception) {
            ApiResponse.Error(0, e.message ?: "Unknown error")
        }
    }
}

/**
 * ApiResponse - Sealed class for representing API response states
 * 
 * This sealed class provides a type-safe way to handle API responses,
 * ensuring that both success and error cases are properly handled.
 * 
 * @param T The type of data returned on success
 */
sealed class ApiResponse<out T> {
    /**
     * Successful API response containing the requested data
     * 
     * @param data The deserialized response data
     */
    data class Success<T>(val data: T) : ApiResponse<T>()
    
    /**
     * Failed API response containing error information
     * 
     * @param code HTTP status code or 0 for network errors
     * @param message Error message describing what went wrong
     */
    data class Error(val code: Int, val message: String) : ApiResponse<Nothing>()
    
    /**
     * Transform the success data to a different type
     * 
     * @param R The target type for transformation
     * @param transform Function to transform the success data
     * @return New ApiResponse with transformed data or original error
     */
    inline fun <R> map(transform: (T) -> R): ApiResponse<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> this
        }
    }
    
    /**
     * Execute an action if the response is successful
     * 
     * @param action Function to execute with the success data
     * @return The original ApiResponse for chaining
     */
    inline fun onSuccess(action: (T) -> Unit): ApiResponse<T> {
        if (this is Success) action(data)
        return this
    }
    
    /**
     * Execute an action if the response is an error
     * 
     * @param action Function to execute with error code and message
     * @return The original ApiResponse for chaining
     */
    inline fun onError(action: (Int, String) -> Unit): ApiResponse<T> {
        if (this is Error) action(code, message)
        return this
    }
}