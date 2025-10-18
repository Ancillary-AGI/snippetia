package com.snippetia.controller

import com.snippetia.dto.UserProfileResponse
import com.snippetia.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

/**
 * UserController - REST API endpoints for user management operations
 * 
 * This controller provides comprehensive user management functionality including:
 * - User profile retrieval and management
 * - Administrative user operations
 * - Username and email availability checking
 * - Activity tracking and analytics
 * 
 * Security Features:
 * - JWT-based authentication required for most endpoints
 * - Role-based access control (RBAC) for admin operations
 * - Input validation and sanitization
 * - Rate limiting and abuse prevention
 * 
 * API Documentation:
 * - OpenAPI 3.0 annotations for automatic documentation
 * - Comprehensive error response definitions
 * - Request/response examples and schemas
 * 
 * Endpoints:
 * - GET /profile - Get current user's profile
 * - GET /{userId}/profile - Get any user's public profile
 * - GET /username/{username} - Get profile by username
 * - GET / - List all users (admin only)
 * - DELETE /{userId} - Delete user account (admin only)
 * - POST /{userId}/activity - Update user activity
 * - GET /check/username/{username} - Check username availability
 * - GET /check/email/{email} - Check email availability
 * 
 * @author Snippetia Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "User profile and management operations")
@SecurityRequirement(name = "bearerAuth")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile", description = "Retrieve the profile of the currently authenticated user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "404", description = "User not found")
    ])
    fun getCurrentUserProfile(authentication: Authentication): ResponseEntity<UserProfileResponse> {
        val username = authentication.name
        val user = userService.getUserByUsername(username)
        val profile = userService.getUserProfile(user.id!!)
        return ResponseEntity.ok(profile)
    }

    @GetMapping("/{userId}/profile")
    @Operation(summary = "Get user profile by ID", description = "Retrieve a user's public profile information")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        ApiResponse(responseCode = "404", description = "User not found")
    ])
    fun getUserProfile(
        @Parameter(description = "User ID", required = true)
        @PathVariable userId: Long
    ): ResponseEntity<UserProfileResponse> {
        val profile = userService.getUserProfile(userId)
        return ResponseEntity.ok(profile)
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user profile by username", description = "Retrieve a user's public profile by username")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        ApiResponse(responseCode = "404", description = "User not found")
    ])
    fun getUserByUsername(
        @Parameter(description = "Username", required = true)
        @PathVariable username: String
    ): ResponseEntity<UserProfileResponse> {
        val user = userService.getUserByUsername(username)
        val profile = userService.getUserProfile(user.id!!)
        return ResponseEntity.ok(profile)
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieve paginated list of all users (Admin only)")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    ])
    fun getAllUsers(
        @Parameter(description = "Pagination parameters")
        pageable: Pageable
    ): ResponseEntity<Page<UserProfileResponse>> {
        val users = userService.getAllUsers(pageable)
        val profiles = users.map { user ->
            userService.getUserProfile(user.id!!)
        }
        return ResponseEntity.ok(profiles)
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Delete a user account (Admin only)")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "User deleted successfully"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
        ApiResponse(responseCode = "404", description = "User not found")
    ])
    fun deleteUser(
        @Parameter(description = "User ID", required = true)
        @PathVariable userId: Long
    ): ResponseEntity<Void> {
        userService.deleteUser(userId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{userId}/activity")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    @Operation(summary = "Update user activity", description = "Update user's last activity timestamp")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Activity updated successfully"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "403", description = "Forbidden"),
        ApiResponse(responseCode = "404", description = "User not found")
    ])
    fun updateUserActivity(
        @Parameter(description = "User ID", required = true)
        @PathVariable userId: Long
    ): ResponseEntity<Void> {
        userService.updateLastActivity(userId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/check/username/{username}")
    @Operation(summary = "Check username availability", description = "Check if a username is available")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Username availability checked")
    ])
    fun checkUsernameAvailability(
        @Parameter(description = "Username to check", required = true)
        @PathVariable username: String
    ): ResponseEntity<Map<String, Boolean>> {
        val exists = userService.existsByUsername(username)
        return ResponseEntity.ok(mapOf("available" to !exists))
    }

    @GetMapping("/check/email/{email}")
    @Operation(summary = "Check email availability", description = "Check if an email is available")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Email availability checked")
    ])
    fun checkEmailAvailability(
        @Parameter(description = "Email to check", required = true)
        @PathVariable email: String
    ): ResponseEntity<Map<String, Boolean>> {
        val exists = userService.existsByEmail(email)
        return ResponseEntity.ok(mapOf("available" to !exists))
    }
}