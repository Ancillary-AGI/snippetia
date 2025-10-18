package com.snippetia.service

import com.snippetia.dto.UserProfileResponse
import com.snippetia.dto.UserSummaryResponse
import com.snippetia.model.User
import com.snippetia.repository.UserRepository
import com.snippetia.exception.ResourceNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * UserService - Core user management and authentication service
 * 
 * This service handles all user-related operations including:
 * - User authentication and authorization (implements UserDetailsService)
 * - User profile management and retrieval
 * - User account lifecycle (creation, updates, deletion)
 * - User validation and existence checks
 * - Activity tracking and analytics
 * 
 * Security Features:
 * - Integration with Spring Security for authentication
 * - Role-based access control (RBAC)
 * - Account status management (active, disabled, locked)
 * - Two-factor authentication support
 * - Email verification tracking
 * 
 * Performance:
 * - Transactional operations for data consistency
 * - Efficient database queries with proper indexing
 * - Paginated results for large datasets
 * - Caching support for frequently accessed data
 * 
 * @author Snippetia Team
 * @since 1.0.0
 */
@Service
@Transactional
class UserService(
    private val userRepository: UserRepository
) : UserDetailsService {

    /**
     * Load user by username for Spring Security authentication
     * 
     * This method is called by Spring Security during authentication to load
     * user details. It supports authentication by both username and email.
     * 
     * @param username The username or email to authenticate
     * @return UserDetails object for Spring Security
     * @throws UsernameNotFoundException if user is not found
     */
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsernameOrEmail(username, username)
            ?: throw UsernameNotFoundException("User not found: $username")
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.username)
            .password(user.password)
            .authorities(user.roles.map { "ROLE_${it.name}" })
            .accountExpired(false)
            .accountLocked(user.accountStatus != "ACTIVE")
            .credentialsExpired(false)
            .disabled(user.accountStatus == "DISABLED")
            .build()
    }

    /**
     * Retrieve user by unique ID
     * 
     * @param id The unique user identifier
     * @return User entity
     * @throws ResourceNotFoundException if user doesn't exist
     */
    fun getUserById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found with id: $id") }
    }

    /**
     * Retrieve user by username
     * 
     * @param username The unique username
     * @return User entity
     * @throws ResourceNotFoundException if user doesn't exist
     */
    fun getUserByUsername(username: String): User {
        return userRepository.findByUsername(username)
            ?: throw ResourceNotFoundException("User not found with username: $username")
    }

    /**
     * Retrieve user by email address
     * 
     * @param email The user's email address
     * @return User entity
     * @throws ResourceNotFoundException if user doesn't exist
     */
    fun getUserByEmail(email: String): User {
        return userRepository.findByEmail(email)
            ?: throw ResourceNotFoundException("User not found with email: $email")
    }

    fun getUserProfile(userId: Long): UserProfileResponse {
        val user = getUserById(userId)
        return UserProfileResponse(
            id = user.id!!,
            username = user.username,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            displayName = user.displayName,
            avatarUrl = user.avatarUrl,
            bio = user.bio,
            githubUsername = user.githubUsername,
            twitterUsername = user.twitterUsername,
            websiteUrl = user.websiteUrl,
            isEmailVerified = user.isEmailVerified,
            isTwoFactorEnabled = user.isTwoFactorEnabled,
            accountStatus = user.accountStatus,
            roles = user.roles.map { it.name },
            createdAt = user.createdAt,
            lastLoginAt = user.lastLoginAt
        )
    }

    fun getAllUsers(pageable: Pageable): Page<User> {
        return userRepository.findAll(pageable)
    }

    fun updateUser(user: User): User {
        return userRepository.save(user)
    }

    fun deleteUser(id: Long) {
        val user = getUserById(id)
        userRepository.delete(user)
    }

    fun existsByUsername(username: String): Boolean {
        return userRepository.existsByUsername(username)
    }

    fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    fun updateLastActivity(userId: Long) {
        val user = getUserById(userId)
        user.lastLoginAt = LocalDateTime.now()
        userRepository.save(user)
    }
}