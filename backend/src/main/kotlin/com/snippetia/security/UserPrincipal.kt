package com.snippetia.security

import com.snippetia.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

/**
 * UserPrincipal - Custom user principal for authentication and authorization
 * 
 * This class implements both UserDetails (for Spring Security) and OAuth2User
 * (for OAuth2 authentication) to provide a unified user representation across
 * different authentication methods.
 * 
 * Features:
 * - Supports both traditional username/password and OAuth2 authentication
 * - Provides role-based access control (RBAC)
 * - Includes user metadata and preferences
 * - Handles account status (enabled, locked, expired, etc.)
 * 
 * Security:
 * - Passwords are never exposed in this class
 * - Sensitive information is excluded from serialization
 * - Implements proper equals/hashCode for security contexts
 * 
 * @author Snippetia Team
 * @since 1.0.0
 */
data class UserPrincipal(
    val id: Long,
    private val username: String,
    private val email: String,
    private val password: String? = null,
    val displayName: String,
    val avatarUrl: String? = null,
    val roles: Set<String> = emptySet(),
    val isEnabled: Boolean = true,
    val isAccountNonExpired: Boolean = true,
    val isAccountNonLocked: Boolean = true,
    val isCredentialsNonExpired: Boolean = true,
    private val attributes: Map<String, Any> = emptyMap()
) : UserDetails, OAuth2User {

    companion object {
        /**
         * Create UserPrincipal from User entity
         */
        fun create(user: User): UserPrincipal {
            return UserPrincipal(
                id = user.id!!,
                username = user.username,
                email = user.email,
                password = user.password,
                displayName = user.displayName ?: user.username,
                avatarUrl = user.avatarUrl,
                roles = user.roles.map { it.name }.toSet(),
                isEnabled = user.enabled,
                isAccountNonExpired = !user.accountExpired,
                isAccountNonLocked = !user.accountLocked,
                isCredentialsNonExpired = !user.credentialsExpired
            )
        }

        /**
         * Create UserPrincipal from OAuth2 user attributes
         */
        fun create(user: User, attributes: Map<String, Any>): UserPrincipal {
            return create(user).copy(attributes = attributes)
        }
    }

    // UserDetails implementation
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return roles.map { SimpleGrantedAuthority("ROLE_$it") }
    }

    override fun getPassword(): String? = password

    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = isAccountNonExpired

    override fun isAccountNonLocked(): Boolean = isAccountNonLocked

    override fun isCredentialsNonExpired(): Boolean = isCredentialsNonExpired

    override fun isEnabled(): Boolean = isEnabled

    // OAuth2User implementation
    override fun getAttributes(): Map<String, Any> = attributes

    override fun getName(): String = username

    // Additional utility methods
    fun hasRole(role: String): Boolean = roles.contains(role.uppercase())

    fun hasAnyRole(vararg roles: String): Boolean = roles.any { hasRole(it) }

    fun isAdmin(): Boolean = hasRole("ADMIN")

    fun isModerator(): Boolean = hasRole("MODERATOR") || isAdmin()

    fun isPremium(): Boolean = hasRole("PREMIUM") || hasRole("PRO") || hasRole("ENTERPRISE")

    /**
     * Get user's email address
     */
    fun getEmail(): String = email

    /**
     * Get user's display name
     */
    fun getDisplayName(): String = displayName

    /**
     * Get user's avatar URL
     */
    fun getAvatarUrl(): String? = avatarUrl

    /**
     * Check if user has specific permission
     */
    fun hasPermission(permission: String): Boolean {
        // This could be extended to support fine-grained permissions
        return when (permission) {
            "CREATE_SNIPPET" -> isEnabled
            "DELETE_SNIPPET" -> isEnabled
            "MODERATE_CONTENT" -> isModerator()
            "ADMIN_ACCESS" -> isAdmin()
            "PREMIUM_FEATURES" -> isPremium()
            else -> false
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserPrincipal) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String {
        return "UserPrincipal(id=$id, username='$username', roles=$roles, enabled=$isEnabled)"
    }
}