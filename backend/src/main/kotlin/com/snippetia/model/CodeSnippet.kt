package com.snippetia.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * CodeSnippet - Core entity representing a code snippet in the platform
 * 
 * This entity represents the main content type in Snippetia - code snippets
 * that users create, share, and collaborate on. It includes comprehensive
 * metadata, social features, and security scanning capabilities.
 * 
 * Features:
 * - Multi-language code support with syntax highlighting
 * - Social interactions (likes, forks, views)
 * - Tag-based categorization and discovery
 * - Version control and history tracking
 * - Privacy controls (public/private visibility)
 * - Content moderation and security scanning
 * - Fork relationships for code collaboration
 * 
 * Security:
 * - Automatic virus and malware scanning
 * - Security vulnerability detection
 * - Content moderation with hiding capability
 * - Author ownership and permission controls
 * 
 * Analytics:
 * - View count tracking for popularity metrics
 * - Like count for engagement measurement
 * - Fork count for collaboration tracking
 * - Creation and modification timestamps
 * 
 * @author Snippetia Team
 * @since 1.0.0
 */
@Entity
@Table(name = "code_snippets")
@EntityListeners(AuditingEntityListener::class)
data class CodeSnippet(
    /** Unique identifier for the code snippet */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    /** Human-readable title for the snippet (required) */
    @Column(nullable = false)
    var title: String,

    /** Optional description explaining what the code does */
    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    /** The actual code content (stored as TEXT for large snippets) */
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    var content: String,

    /** Programming language identifier (e.g., "kotlin", "javascript", "python") */
    @Column(name = "language", nullable = false)
    var language: String,

    /** Tags for categorization and discovery (stored in separate table) */
    @ElementCollection
    @CollectionTable(name = "snippet_tags", joinColumns = [JoinColumn(name = "snippet_id")])
    @Column(name = "tag")
    var tags: MutableSet<String> = mutableSetOf(),

    /** Visibility setting - true for public, false for private */
    @Column(name = "is_public")
    var isPublic: Boolean = true,

    /** Number of times this snippet has been viewed */
    @Column(name = "view_count")
    var viewCount: Long = 0,

    /** Number of likes/stars this snippet has received */
    @Column(name = "like_count")
    var likeCount: Long = 0,

    /** Number of times this snippet has been forked */
    @Column(name = "fork_count")
    var forkCount: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    var author: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forked_from_id")
    var forkedFrom: CodeSnippet? = null,

    @Column(name = "is_hidden")
    var isHidden: Boolean = false,

    @Column(name = "moderation_reason")
    var moderationReason: String? = null,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * Virus scan status for uploaded content
 * 
 * Tracks the status of virus/malware scanning for code snippets
 * to ensure platform security and user safety.
 */
enum class VirusScanStatus {
    /** Scan has been queued but not started */
    PENDING,
    
    /** Currently being scanned by antivirus engine */
    SCANNING,
    
    /** No threats detected - content is safe */
    CLEAN,
    
    /** Malware or virus detected - content blocked */
    INFECTED,
    
    /** Error occurred during scanning process */
    ERROR
}

/**
 * Security vulnerability scan status
 * 
 * Tracks the status of security vulnerability scanning for code
 * to identify potential security issues and unsafe patterns.
 */
enum class SecurityScanStatus {
    /** Security scan queued but not started */
    PENDING,
    
    /** Currently being analyzed for vulnerabilities */
    SCANNING,
    
    /** No security vulnerabilities found */
    SAFE,
    
    /** Security vulnerabilities detected */
    VULNERABLE,
    
    /** Error occurred during security analysis */
    ERROR
}

@Entity
@Table(name = "snippet_versions")
@EntityListeners(AuditingEntityListener::class)
data class SnippetVersion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "version_number", nullable = false)
    val versionNumber: String,

    @Column(name = "code_content", columnDefinition = "TEXT", nullable = false)
    val codeContent: String,

    @Column(name = "change_description", columnDefinition = "TEXT")
    val changeDescription: String? = null,

    @Column(name = "file_size")
    val fileSize: Long = 0,

    @Column(name = "checksum")
    val checksum: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snippet_id", nullable = false)
    val snippet: CodeSnippet,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "categories")
@EntityListeners(AuditingEntityListener::class)
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val name: String,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Column(name = "icon_url")
    val iconUrl: String? = null,

    @Column(name = "color_code")
    val colorCode: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    val parent: Category? = null,

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val children: List<Category> = listOf(),

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)