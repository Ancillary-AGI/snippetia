package com.snippetia.security

import org.springframework.security.core.annotation.AuthenticationPrincipal

/**
 * CurrentUser - Annotation for injecting the current authenticated user
 * 
 * This annotation is used to inject the current authenticated user's information
 * into controller methods. It provides a clean way to access user details without
 * manually extracting them from the SecurityContext.
 * 
 * Usage:
 * ```kotlin
 * @GetMapping("/profile")
 * fun getProfile(@CurrentUser user: UserPrincipal): ResponseEntity<UserProfile> {
 *     // Use user.id, user.username, etc.
 * }
 * ```
 * 
 * The annotation resolves to the UserPrincipal object stored in the SecurityContext
 * after successful authentication via JWT, OAuth2, or other authentication methods.
 * 
 * @author Snippetia Team
 * @since 1.0.0
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@AuthenticationPrincipal
annotation class CurrentUser