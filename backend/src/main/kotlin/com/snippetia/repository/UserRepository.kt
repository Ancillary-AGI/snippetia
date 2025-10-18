package com.snippetia.repository

import com.snippetia.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * UserRepository - Data access layer for User entities
 * 
 * This repository provides comprehensive data access methods for user management,
 * authentication, analytics, and social features. It extends JpaRepository to
 * provide standard CRUD operations plus custom query methods.
 * 
 * Features:
 * - User authentication and lookup methods
 * - Email verification and password reset token handling
 * - Analytics queries for user growth and engagement
 * - Search functionality with fuzzy matching
 * - Trending user discovery algorithms
 * - Social features support (followers, activity tracking)
 * 
 * Query Optimization:
 * - Indexed queries on username, email, and tokens
 * - Efficient pagination for large datasets
 * - Optimized search with LIKE queries and proper indexing
 * - Analytics queries with date range filtering
 * 
 * Security:
 * - Safe parameter binding to prevent SQL injection
 * - Token-based queries for secure operations
 * - Case-insensitive search for better UX
 * 
 * @author Snippetia Team
 * @since 1.0.0
 */
@Repository
interface UserRepository : JpaRepository<User, Long> {
    
    /**
     * Find user by exact username match
     * 
     * @param username The username to search for
     * @return User entity if found, null otherwise
     */
    fun findByUsername(username: String): User?
    
    /**
     * Find user by exact email match
     * 
     * @param email The email address to search for
     * @return User entity if found, null otherwise
     */
    fun findByEmail(email: String): User?
    
    /**
     * Find user by username or email (used for login)
     * 
     * This method allows users to log in with either their username or email address,
     * providing flexibility and better user experience.
     * 
     * @param usernameOrEmail The username or email to search for
     * @param usernameOrEmail2 Duplicate parameter for JPA query binding
     * @return User entity if found, null otherwise
     */
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    fun findByUsernameOrEmail(@Param("usernameOrEmail") usernameOrEmail: String, @Param("usernameOrEmail") usernameOrEmail2: String): User?
    
    /**
     * Check if username already exists (for registration validation)
     * 
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    fun existsByUsername(username: String): Boolean
    
    /**
     * Check if email already exists (for registration validation)
     * 
     * @param email The email address to check
     * @return true if email exists, false otherwise
     */
    fun existsByEmail(email: String): Boolean
    
    /**
     * Find user by email verification token
     * 
     * Used during the email verification process to validate tokens
     * and activate user accounts.
     * 
     * @param token The email verification token
     * @return User entity if token is valid, null otherwise
     */
    fun findByEmailVerificationToken(token: String): User?
    
    /**
     * Find user by password reset token
     * 
     * Used during the password reset process to validate tokens
     * and allow password changes.
     * 
     * @param token The password reset token
     * @return User entity if token is valid, null otherwise
     */
    fun findByPasswordResetToken(token: String): User?
    
    // ===== ANALYTICS QUERIES =====
    
    /**
     * Count users created after a specific date
     * 
     * Used for growth analytics and new user registration metrics.
     * 
     * @param date The cutoff date for counting new users
     * @return Number of users created after the specified date
     */
    fun countByCreatedAtAfter(date: LocalDateTime): Long
    
    @Query("""
        SELECT COUNT(DISTINCT u) FROM User u 
        WHERE u.lastLoginAt >= :since
    """)
    fun countActiveUsersSince(@Param("since") since: LocalDateTime): Long
    
    @Query("""
        SELECT u FROM User u 
        WHERE u.createdAt >= :since 
        ORDER BY u.followerCount DESC
    """)
    fun findTrendingUsers(@Param("since") since: LocalDateTime, pageable: Pageable): List<User>
    
    @Query("""
        SELECT u FROM User u 
        WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) 
        OR LOWER(u.displayName) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY u.followerCount DESC
    """)
    fun searchUsers(@Param("query") query: String, pageable: Pageable): Page<User>
    
    fun findTop10ByOrderByFollowerCountDesc(): List<User>
}