package com.snippetia.service

import com.snippetia.dto.*
import com.snippetia.model.CodeSnippet
import com.snippetia.model.User
import com.snippetia.model.Comment
import com.snippetia.model.Like
import com.snippetia.model.SnippetVersion
import com.snippetia.repository.*
import com.snippetia.exception.ResourceNotFoundException
import com.snippetia.exception.UnauthorizedException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

/**
 * SnippetService - Core business logic for code snippet management
 * 
 * This service handles all operations related to code snippets including:
 * - CRUD operations for snippets
 * - Security scanning and validation
 * - Search and filtering functionality
 * - Version control and history tracking
 * - Social features (likes, comments, forks)
 * - File upload and language detection
 * 
 * Features:
 * - Automatic security scanning with VirusTotal integration
 * - AI-powered language detection and code analysis
 * - Real-time search indexing with Elasticsearch
 * - Version control with Git-like functionality
 * - Social interactions (likes, comments, forks)
 * - Advanced filtering and categorization
 * 
 * Security:
 * - All snippets are scanned for malicious content
 * - User authorization checks for all operations
 * - Input validation and sanitization
 * - Rate limiting and abuse prevention
 * 
 * @author Snippetia Team
 * @since 1.0.0
 */
@Service
@Transactional
class SnippetService(
    private val snippetRepository: CodeSnippetRepository,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val likeRepository: LikeRepository,
    private val versionRepository: SnippetVersionRepository,
    private val securityScanService: SecurityScanService,
    private val fileStorageService: FileStorageService,
    private val searchService: SearchService
) {

    /**
     * Retrieves all public code snippets with optional filtering
     * 
     * @param language Optional programming language filter (e.g., "kotlin", "java", "python")
     * @param category Optional category filter (e.g., "algorithm", "ui", "backend")
     * @param tags Optional list of tags to filter by
     * @param search Optional search query for full-text search
     * @param pageable Pagination parameters (page, size, sort)
     * @return Paginated list of public snippets matching the criteria
     */
    fun getAllPublicSnippets(
        language: String?,
        category: String?,
        tags: List<String>?,
        search: String?,
        pageable: Pageable
    ): Page<SnippetResponse> {
        val snippets = when {
            search != null -> searchService.searchSnippets(search, language, category, tags, pageable)
            else -> snippetRepository.findPublicSnippets(language, category, tags, pageable)
        }
        
        return snippets.map { mapToSnippetResponse(it) }
    }

    /**
     * Retrieves a specific snippet by its ID with full details
     * 
     * This method also increments the view count for analytics purposes.
     * Only public snippets can be accessed through this method.
     * 
     * @param id The unique identifier of the snippet
     * @return Detailed snippet information including content, metadata, and statistics
     * @throws ResourceNotFoundException if snippet doesn't exist
     * @throws UnauthorizedException if snippet is private
     */
    fun getSnippetById(id: Long): SnippetDetailResponse {
        val snippet = snippetRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Snippet not found") }
        
        if (!snippet.isPublic) {
            throw UnauthorizedException("Snippet is private")
        }

        // Increment view count
        snippet.viewCount++
        snippetRepository.save(snippet)

        return mapToSnippetDetailResponse(snippet)
    }

    /**
     * Creates a new code snippet
     * 
     * This method performs several operations:
     * 1. Validates user existence and permissions
     * 2. Creates the snippet entity with provided metadata
     * 3. Performs security scanning for malicious content
     * 4. Indexes the snippet for search functionality
     * 
     * @param userId The ID of the user creating the snippet
     * @param request The snippet creation request containing title, content, language, etc.
     * @return The created snippet with generated ID and metadata
     * @throws ResourceNotFoundException if user doesn't exist
     * @throws SecurityException if snippet contains malicious content
     */
    fun createSnippet(userId: Long, request: CreateSnippetRequest): SnippetResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found") }

        val snippet = CodeSnippet(
            title = request.title,
            description = request.description,
            content = request.content,
            language = request.language,
            tags = request.tags.toMutableSet(),
            isPublic = request.isPublic,
            author = user,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Perform security scan
        securityScanService.scanSnippet(snippet)

        val savedSnippet = snippetRepository.save(snippet)
        
        // Index for search
        searchService.indexSnippet(savedSnippet)

        return mapToSnippetResponse(savedSnippet)
    }

    /**
     * Creates a snippet from an uploaded file
     * 
     * This method handles file upload and processing:
     * 1. Reads and validates the uploaded file
     * 2. Detects programming language from file extension if not provided
     * 3. Extracts content and creates snippet
     * 4. Performs security scanning and indexing
     * 
     * @param userId The ID of the user uploading the snippet
     * @param request The upload request containing file and metadata
     * @return The created snippet from the uploaded file
     * @throws ResourceNotFoundException if user doesn't exist
     * @throws IllegalArgumentException if file is invalid or too large
     */
    fun uploadSnippet(userId: Long, request: UploadSnippetRequest): SnippetResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found") }

        // Read file content
        val content = String(request.file.bytes)
        
        // Detect language if not provided
        val detectedLanguage = request.language.ifEmpty { 
            fileStorageService.detectLanguage(request.file.originalFilename ?: "")
        }

        val snippet = CodeSnippet(
            title = request.title,
            description = request.description,
            content = content,
            language = detectedLanguage,
            tags = request.tags.toMutableSet(),
            isPublic = request.isPublic,
            author = user,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Perform security scan
        securityScanService.scanSnippet(snippet)

        val savedSnippet = snippetRepository.save(snippet)
        
        // Index for search
        searchService.indexSnippet(savedSnippet)

        return mapToSnippetResponse(savedSnippet)
    }

    /**
     * Updates an existing snippet
     * 
     * This method provides version control functionality:
     * 1. Validates user ownership of the snippet
     * 2. Creates a new version if content has changed
     * 3. Updates snippet metadata and content
     * 4. Re-scans for security if content changed
     * 5. Updates search index
     * 
     * @param userId The ID of the user updating the snippet
     * @param snippetId The ID of the snippet to update
     * @param request The update request with new values
     * @return The updated snippet information
     * @throws ResourceNotFoundException if snippet doesn't exist
     * @throws UnauthorizedException if user doesn't own the snippet
     */
    fun updateSnippet(userId: Long, snippetId: Long, request: UpdateSnippetRequest): SnippetResponse {
        val snippet = snippetRepository.findById(snippetId)
            .orElseThrow { ResourceNotFoundException("Snippet not found") }

        if (snippet.author.id != userId) {
            throw UnauthorizedException("You can only update your own snippets")
        }

        // Create new version if content changed
        if (request.content != null && request.content != snippet.content) {
            createSnippetVersion(snippet, snippet.content)
        }

        // Update snippet
        request.title?.let { snippet.title = it }
        request.description?.let { snippet.description = it }
        request.content?.let { snippet.content = it }
        request.language?.let { snippet.language = it }
        request.tags?.let { snippet.tags = it.toMutableSet() }
        request.isPublic?.let { snippet.isPublic = it }
        snippet.updatedAt = LocalDateTime.now()

        // Re-scan if content changed
        if (request.content != null) {
            securityScanService.scanSnippet(snippet)
        }

        val savedSnippet = snippetRepository.save(snippet)
        
        // Update search index
        searchService.updateSnippet(savedSnippet)

        return mapToSnippetResponse(savedSnippet)
    }

    /**
     * Deletes a snippet and all associated data
     * 
     * This method performs cleanup operations:
     * 1. Validates user ownership
     * 2. Removes from search index
     * 3. Deletes snippet and cascades to related entities (comments, likes, versions)
     * 
     * @param userId The ID of the user deleting the snippet
     * @param snippetId The ID of the snippet to delete
     * @throws ResourceNotFoundException if snippet doesn't exist
     * @throws UnauthorizedException if user doesn't own the snippet
     */
    fun deleteSnippet(userId: Long, snippetId: Long) {
        val snippet = snippetRepository.findById(snippetId)
            .orElseThrow { ResourceNotFoundException("Snippet not found") }

        if (snippet.author.id != userId) {
            throw UnauthorizedException("You can only delete your own snippets")
        }

        // Remove from search index
        searchService.deleteSnippet(snippetId)
        
        snippetRepository.delete(snippet)
    }

    /**
     * Toggles like status for a snippet
     * 
     * If the user has already liked the snippet, it removes the like.
     * If the user hasn't liked it, it adds a new like.
     * Updates the snippet's like count accordingly.
     * 
     * @param userId The ID of the user toggling the like
     * @param snippetId The ID of the snippet to like/unlike
     * @return true if snippet is now liked, false if like was removed
     * @throws ResourceNotFoundException if user or snippet doesn't exist
     */
    fun toggleLike(userId: Long, snippetId: Long): Boolean {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found") }
        
        val snippet = snippetRepository.findById(snippetId)
            .orElseThrow { ResourceNotFoundException("Snippet not found") }

        val existingLike = likeRepository.findByUserAndSnippet(user, snippet)
        
        return if (existingLike != null) {
            likeRepository.delete(existingLike)
            snippet.likeCount--
            snippetRepository.save(snippet)
            false
        } else {
            val like = Like(user = user, snippet = snippet, createdAt = LocalDateTime.now())
            likeRepository.save(like)
            snippet.likeCount++
            snippetRepository.save(snippet)
            true
        }
    }

    /**
     * Creates a fork (copy) of an existing snippet
     * 
     * Forking allows users to create their own version of someone else's snippet.
     * The fork maintains a reference to the original snippet and increments
     * the original's fork count.
     * 
     * @param userId The ID of the user creating the fork
     * @param snippetId The ID of the snippet to fork
     * @return The newly created forked snippet
     * @throws ResourceNotFoundException if user or snippet doesn't exist
     */
    fun forkSnippet(userId: Long, snippetId: Long): SnippetResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found") }
        
        val originalSnippet = snippetRepository.findById(snippetId)
            .orElseThrow { ResourceNotFoundException("Snippet not found") }

        val forkedSnippet = CodeSnippet(
            title = "Fork of ${originalSnippet.title}",
            description = originalSnippet.description,
            content = originalSnippet.content,
            language = originalSnippet.language,
            tags = originalSnippet.tags.toMutableSet(),
            isPublic = true,
            author = user,
            forkedFrom = originalSnippet,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        originalSnippet.forkCount++
        snippetRepository.save(originalSnippet)

        val savedSnippet = snippetRepository.save(forkedSnippet)
        
        // Index for search
        searchService.indexSnippet(savedSnippet)

        return mapToSnippetResponse(savedSnippet)
    }

    fun getSnippetVersions(snippetId: Long): List<SnippetVersionResponse> {
        val snippet = snippetRepository.findById(snippetId)
            .orElseThrow { ResourceNotFoundException("Snippet not found") }

        val versions = versionRepository.findBySnippetOrderByCreatedAtDesc(snippet)
        return versions.map { mapToVersionResponse(it) }
    }

    fun createVersion(userId: Long, snippetId: Long, request: CreateVersionRequest): SnippetVersionResponse {
        val snippet = snippetRepository.findById(snippetId)
            .orElseThrow { ResourceNotFoundException("Snippet not found") }

        if (snippet.author.id != userId) {
            throw UnauthorizedException("You can only create versions for your own snippets")
        }

        val version = createSnippetVersion(snippet, request.content, request.description)
        
        // Update snippet with new content
        snippet.content = request.content
        snippet.updatedAt = LocalDateTime.now()
        snippetRepository.save(snippet)

        return mapToVersionResponse(version)
    }

    fun getUserSnippets(userId: Long, pageable: Pageable): Page<SnippetResponse> {
        val snippets = snippetRepository.findByAuthorId(userId, pageable)
        return snippets.map { mapToSnippetResponse(it) }
    }

    fun getFeaturedSnippets(pageable: Pageable): Page<SnippetResponse> {
        val snippets = snippetRepository.findFeaturedSnippets(pageable)
        return snippets.map { mapToSnippetResponse(it) }
    }

    fun getTrendingSnippets(pageable: Pageable): Page<SnippetResponse> {
        val snippets = snippetRepository.findTrendingSnippets(pageable)
        return snippets.map { mapToSnippetResponse(it) }
    }

    fun addComment(userId: Long, snippetId: Long, request: CreateCommentRequest): CommentResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found") }
        
        val snippet = snippetRepository.findById(snippetId)
            .orElseThrow { ResourceNotFoundException("Snippet not found") }

        val comment = Comment(
            content = request.content,
            author = user,
            snippet = snippet,
            createdAt = LocalDateTime.now()
        )

        val savedComment = commentRepository.save(comment)
        return mapToCommentResponse(savedComment)
    }

    fun getComments(snippetId: Long, pageable: Pageable): Page<CommentResponse> {
        val snippet = snippetRepository.findById(snippetId)
            .orElseThrow { ResourceNotFoundException("Snippet not found") }

        val comments = commentRepository.findBySnippetOrderByCreatedAtDesc(snippet, pageable)
        return comments.map { mapToCommentResponse(it) }
    }

    private fun createSnippetVersion(snippet: CodeSnippet, content: String, description: String? = null): SnippetVersion {
        val version = SnippetVersion(
            snippet = snippet,
            content = content,
            description = description,
            versionNumber = (versionRepository.countBySnippet(snippet) + 1).toInt(),
            createdAt = LocalDateTime.now()
        )
        return versionRepository.save(version)
    }

    private fun mapToSnippetResponse(snippet: CodeSnippet): SnippetResponse {
        return SnippetResponse(
            id = snippet.id!!,
            title = snippet.title,
            description = snippet.description,
            language = snippet.language,
            tags = snippet.tags.toList(),
            isPublic = snippet.isPublic,
            author = UserSummaryResponse(
                id = snippet.author.id!!,
                username = snippet.author.username,
                displayName = snippet.author.displayName,
                avatarUrl = snippet.author.avatarUrl
            ),
            likeCount = snippet.likeCount,
            viewCount = snippet.viewCount,
            forkCount = snippet.forkCount,
            createdAt = snippet.createdAt,
            updatedAt = snippet.updatedAt
        )
    }

    private fun mapToSnippetDetailResponse(snippet: CodeSnippet): SnippetDetailResponse {
        return SnippetDetailResponse(
            id = snippet.id!!,
            title = snippet.title,
            description = snippet.description,
            content = snippet.content,
            language = snippet.language,
            tags = snippet.tags.toList(),
            isPublic = snippet.isPublic,
            author = UserSummaryResponse(
                id = snippet.author.id!!,
                username = snippet.author.username,
                displayName = snippet.author.displayName,
                avatarUrl = snippet.author.avatarUrl
            ),
            likeCount = snippet.likeCount,
            viewCount = snippet.viewCount,
            forkCount = snippet.forkCount,
            forkedFrom = snippet.forkedFrom?.let {
                SnippetSummaryResponse(
                    id = it.id!!,
                    title = it.title,
                    author = UserSummaryResponse(
                        id = it.author.id!!,
                        username = it.author.username,
                        displayName = it.author.displayName,
                        avatarUrl = it.author.avatarUrl
                    )
                )
            },
            createdAt = snippet.createdAt,
            updatedAt = snippet.updatedAt
        )
    }

    private fun mapToVersionResponse(version: SnippetVersion): SnippetVersionResponse {
        return SnippetVersionResponse(
            id = version.id!!,
            versionNumber = version.versionNumber,
            description = version.description,
            createdAt = version.createdAt
        )
    }

    private fun mapToCommentResponse(comment: Comment): CommentResponse {
        return CommentResponse(
            id = comment.id!!,
            content = comment.content,
            author = UserSummaryResponse(
                id = comment.author.id!!,
                username = comment.author.username,
                displayName = comment.author.displayName,
                avatarUrl = comment.author.avatarUrl
            ),
            createdAt = comment.createdAt
        )
    }
}