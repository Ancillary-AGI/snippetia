package com.snippetia.data.storage

/**
 * Token storage interface for managing authentication tokens
 */
interface TokenStorage {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
    suspend fun saveRefreshToken(token: String)
    suspend fun getRefreshToken(): String?
    suspend fun clearRefreshToken()
}

/**
 * In-memory implementation of TokenStorage for development
 * In production, this should use platform-specific secure storage
 */
class TokenStorageImpl : TokenStorage {
    private var accessToken: String? = null
    private var refreshToken: String? = null
    
    override suspend fun saveToken(token: String) {
        accessToken = token
    }
    
    override suspend fun getToken(): String? = accessToken
    
    override suspend fun clearToken() {
        accessToken = null
    }
    
    override suspend fun saveRefreshToken(token: String) {
        refreshToken = token
    }
    
    override suspend fun getRefreshToken(): String? = refreshToken
    
    override suspend fun clearRefreshToken() {
        refreshToken = null
    }
}