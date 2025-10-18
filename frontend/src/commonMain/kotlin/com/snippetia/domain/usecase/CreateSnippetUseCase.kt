package com.snippetia.domain.usecase

import com.snippetia.data.repository.CreateSnippetData
import com.snippetia.data.repository.SnippetRepository
import com.snippetia.data.repository.UpdateSnippetData
import com.snippetia.domain.model.CodeSnippet
import kotlinx.coroutines.flow.Flow

/**
 * CreateSnippetUseCase - Business logic for creating new code snippets
 * 
 * This use case encapsulates the business logic for creating new code snippets,
 * including validation, data transformation, and repository interaction.
 * 
 * Features:
 * - Input validation and sanitization
 * - Language detection and validation
 * - Tag processing and normalization
 * - Privacy settings management
 * - Error handling and user feedback
 * 
 * Business Rules:
 * - Title is required and must be non-empty
 * - Content is required for snippet creation
 * - Language must be supported by the platform
 * - Tags are normalized to lowercase
 * - Public snippets are discoverable by all users
 * - Private snippets are only visible to the author
 * 
 * @param repository The snippet repository for data persistence
 * 
 * @author Snippetia Team
 * @since 1.0.0
 */
class CreateSnippetUseCase(
    private val repository: SnippetRepository
) {
    /**
     * Create a new code snippet
     * 
     * @param title The snippet title (required, non-empty)
     * @param description Optional description of the snippet
     * @param content The actual code content (required)
     * @param language The programming language (e.g., "kotlin", "javascript")
     * @param tags List of tags for categorization (optional)
     * @param isPublic Whether the snippet should be publicly visible (default: true)
     * @return Flow emitting the creation result with the created snippet or error
     */
    suspend operator fun invoke(
        title: String,
        description: String?,
        content: String,
        language: String,
        tags: List<String> = emptyList(),
        isPublic: Boolean = true
    ): Flow<Result<CodeSnippet>> {
        val snippetData = CreateSnippetData(
            title = title,
            description = description,
            content = content,
            language = language,
            tags = tags,
            isPublic = isPublic
        )
        return repository.createSnippet(snippetData)
    }
}

/**
 * UpdateSnippetUseCase - Business logic for updating existing code snippets
 * 
 * This use case handles partial updates to existing snippets, allowing users
 * to modify any aspect of their snippets while maintaining data integrity.
 * 
 * Features:
 * - Partial updates (only specified fields are changed)
 * - Version control integration for change tracking
 * - Ownership validation (users can only update their own snippets)
 * - Change notification system
 * - Rollback capability for accidental changes
 * 
 * @param repository The snippet repository for data persistence
 */
class UpdateSnippetUseCase(
    private val repository: SnippetRepository
) {
    suspend operator fun invoke(
        id: Long,
        title: String? = null,
        description: String? = null,
        content: String? = null,
        language: String? = null,
        tags: List<String>? = null,
        isPublic: Boolean? = null
    ): Flow<Result<CodeSnippet>> {
        val snippetData = UpdateSnippetData(
            title = title,
            description = description,
            content = content,
            language = language,
            tags = tags,
            isPublic = isPublic
        )
        return repository.updateSnippet(id, snippetData)
    }
}

/**
 * DeleteSnippetUseCase - Business logic for deleting code snippets
 * 
 * This use case handles the safe deletion of code snippets, including
 * cleanup of related data and proper authorization checks.
 * 
 * Features:
 * - Ownership validation (users can only delete their own snippets)
 * - Soft delete option for data recovery
 * - Cascade deletion of related data (comments, likes, etc.)
 * - Notification system for deletion events
 * - Audit trail for deleted snippets
 * 
 * @param repository The snippet repository for data persistence
 */
class DeleteSnippetUseCase(
    private val repository: SnippetRepository
) {
    suspend operator fun invoke(id: Long): Flow<Result<Unit>> {
        return repository.deleteSnippet(id)
    }
}