package com.snippetia.model

/**
 * ModerationEnums - Enumerations for content moderation system
 * 
 * This file contains all the enums used in the moderation system for
 * categorizing content types, moderation reasons, decisions, and statuses.
 * 
 * @author Snippetia Team
 * @since 1.0.0
 */

/**
 * Types of content that can be moderated
 */
enum class ContentType {
    SNIPPET,        // Code snippets
    COMMENT,        // Comments on snippets
    USER,           // User profiles and accounts
    CHANNEL,        // Developer channels
    EVENT,          // Developer events
    SHOWCASE,       // Developer showcase entries
    REPOSITORY,     // Git repositories
    COMMIT,         // Git commits
    PULL_REQUEST,   // Pull requests
    ISSUE,          // Repository issues
    DISCUSSION      // Community discussions
}

/**
 * Reasons for content moderation
 */
enum class ModerationReason {
    SPAM,                    // Spam content
    INAPPROPRIATE_CONTENT,   // Inappropriate or offensive content
    HARASSMENT,              // Harassment or bullying
    COPYRIGHT_VIOLATION,     // Copyright infringement
    POLICY_VIOLATION,        // Platform policy violation
    MALICIOUS_CODE,          // Malicious or harmful code
    SECURITY_VULNERABILITY,  // Security vulnerabilities
    MISINFORMATION,          // False or misleading information
    DUPLICATE_CONTENT,       // Duplicate or plagiarized content
    OFF_TOPIC,              // Off-topic or irrelevant content
    ADULT_CONTENT,           // Adult or NSFW content
    VIOLENCE,                // Violent content
    HATE_SPEECH,             // Hate speech or discrimination
    DOXXING,                 // Personal information exposure
    PHISHING,                // Phishing attempts
    SCAM,                    // Scam or fraudulent content
    IMPERSONATION,           // Impersonation of others
    SELF_HARM,               // Self-harm content
    ILLEGAL_ACTIVITY,        // Illegal activities
    OTHER                    // Other reasons (requires description)
}

/**
 * Moderation decisions that can be made
 */
enum class ModerationDecision {
    APPROVED,           // Content is acceptable, no action needed
    REJECTED,           // Report is invalid, content is fine
    REQUIRES_ACTION,    // Content violates rules, action required
    ESCALATED,          // Escalated to higher-level moderator
    PENDING_REVIEW,     // Needs additional review
    AUTOMATED_ACTION    // Automated system took action
}

/**
 * Status of moderation reports and actions
 */
enum class ModerationStatus {
    PENDING,        // Awaiting review
    IN_REVIEW,      // Currently being reviewed
    RESOLVED,       // Issue resolved
    DISMISSED,      // Report dismissed as invalid
    ACTION_TAKEN,   // Moderation action was taken
    ESCALATED,      // Escalated to higher authority
    APPEALED,       // User has appealed the decision
    APPEAL_APPROVED, // Appeal was successful
    APPEAL_REJECTED, // Appeal was rejected
    CLOSED          // Case closed
}

/**
 * Severity levels for moderation issues
 */
enum class ModerationSeverity {
    LOW,        // Minor issue, low priority
    MEDIUM,     // Moderate issue, normal priority
    HIGH,       // Serious issue, high priority
    CRITICAL,   // Critical issue, immediate attention required
    URGENT      // Urgent issue, emergency response needed
}

/**
 * Types of moderation actions that can be taken
 */
enum class ModerationActionType {
    WARNING,            // Issue warning to user
    CONTENT_REMOVAL,    // Remove specific content
    CONTENT_EDIT,       // Edit/modify content
    ACCOUNT_SUSPENSION, // Temporarily suspend account
    ACCOUNT_BAN,        // Permanently ban account
    FEATURE_RESTRICTION, // Restrict specific features
    SHADOW_BAN,         // Shadow ban (content not visible to others)
    RATE_LIMIT,         // Apply rate limiting
    QUARANTINE,         // Quarantine content for review
    DEMONETIZATION,     // Remove monetization privileges
    VERIFICATION_REMOVAL, // Remove verification status
    CHANNEL_SUSPENSION, // Suspend channel
    EVENT_CANCELLATION, // Cancel event
    REPOSITORY_ARCHIVE, // Archive repository
    NO_ACTION          // No action taken
}

/**
 * Priority levels for moderation queue
 */
enum class ModerationPriority {
    LOW,        // Low priority, can wait
    NORMAL,     // Normal priority
    HIGH,       // High priority, expedite review
    URGENT,     // Urgent, immediate attention
    EMERGENCY   // Emergency, drop everything
}

/**
 * Categories for automated moderation rules
 */
enum class AutoModerationCategory {
    CONTENT_FILTER,     // Content filtering rules
    SPAM_DETECTION,     // Spam detection rules
    SECURITY_SCAN,      // Security scanning rules
    RATE_LIMITING,      // Rate limiting rules
    DUPLICATE_DETECTION, // Duplicate content detection
    LANGUAGE_FILTER,    // Language and profanity filter
    LINK_VALIDATION,    // Link and URL validation
    IMAGE_ANALYSIS,     // Image content analysis
    CODE_ANALYSIS,      // Code quality and security analysis
    BEHAVIORAL_ANALYSIS // User behavior analysis
}

/**
 * Appeal status for moderation decisions
 */
enum class AppealStatus {
    NOT_APPEALED,   // No appeal submitted
    PENDING,        // Appeal pending review
    IN_REVIEW,      // Appeal being reviewed
    APPROVED,       // Appeal approved, decision reversed
    REJECTED,       // Appeal rejected, decision upheld
    ESCALATED,      // Appeal escalated to higher authority
    WITHDRAWN       // Appeal withdrawn by user
}

/**
 * Trust levels for users in moderation system
 */
enum class UserTrustLevel {
    NEW_USER,       // New user, high scrutiny
    BASIC,          // Basic trust level
    TRUSTED,        // Trusted user, reduced scrutiny
    VERIFIED,       // Verified user, minimal scrutiny
    MODERATOR,      // Community moderator
    ADMIN,          // Platform administrator
    SYSTEM          // System/automated actions
}

/**
 * Moderation queue types
 */
enum class ModerationQueueType {
    REPORTS,        // User reports queue
    AUTOMATED,      // Automated detection queue
    APPEALS,        // Appeals queue
    ESCALATED,      // Escalated cases queue
    HIGH_PRIORITY,  // High priority queue
    SECURITY,       // Security-related queue
    LEGAL,          // Legal compliance queue
    COMMUNITY       // Community moderation queue
}