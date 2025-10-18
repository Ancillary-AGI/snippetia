package com.snippetia.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

// AI Bot Request DTOs
data class BotQueryRequest(
    @field:NotBlank(message = "Query is required")
    @field:Size(max = 2000, message = "Query must not exceed 2000 characters")
    val query: String,
    
    val context: BotContextRequest? = null
)

data class BotContextRequest(
    val snippetId: Long? = null,
    val repositoryId: Long? = null,
    val description: String? = null,
    val language: String? = null
)

data class CodeAnalysisRequest(
    @field:NotBlank(message = "Code is required")
    val code: String,
    
    @field:NotBlank(message = "Language is required")
    val language: String,
    
    val analysisType: String = "full" // full, security, performance, style
)

data class CodeCompletionRequest(
    @field:NotBlank(message = "Code is required")
    val code: String,
    
    @field:NotBlank(message = "Language is required")
    val language: String,
    
    val cursorPosition: Int,
    val maxSuggestions: Int = 5
)

data class CodeExplanationRequest(
    @field:NotBlank(message = "Code is required")
    val code: String,
    
    @field:NotBlank(message = "Language is required")
    val language: String,
    
    val explanationLevel: String = "intermediate" // beginner, intermediate, advanced
)

data class DocumentationRequest(
    @field:NotBlank(message = "Code is required")
    val code: String,
    
    @field:NotBlank(message = "Language is required")
    val language: String,
    
    val docStyle: String = "standard", // standard, jsdoc, javadoc, sphinx
    val includeExamples: Boolean = true
)

data class RefactoringRequest(
    @field:NotBlank(message = "Code is required")
    val code: String,
    
    @field:NotBlank(message = "Language is required")
    val language: String,
    
    val refactoringType: String = "general", // general, performance, readability, security
    val preserveComments: Boolean = true
)

data class CodeReviewRequest(
    @field:NotBlank(message = "Code is required")
    val code: String,
    
    @field:NotBlank(message = "Language is required")
    val language: String,
    
    val reviewType: String = "comprehensive", // quick, comprehensive, security-focused
    val includePositiveFeedback: Boolean = true
)

data class BugFixRequest(
    @field:NotBlank(message = "Code is required")
    val code: String,
    
    @field:NotBlank(message = "Language is required")
    val language: String,
    
    @field:NotBlank(message = "Error description is required")
    val errorDescription: String,
    
    val errorMessage: String? = null,
    val stackTrace: String? = null
)

data class CodeOptimizationRequest(
    @field:NotBlank(message = "Code is required")
    val code: String,
    
    @field:NotBlank(message = "Language is required")
    val language: String,
    
    val optimizationType: String = "performance", // performance, memory, readability
    val targetPlatform: String? = null
)

// AI Bot Response DTOs
data class BotResponse(
    val response: String,
    val confidence: Double,
    val suggestions: List<String> = emptyList(),
    val relatedTopics: List<String> = emptyList(),
    val timestamp: LocalDateTime = LocalDateTime.now()
)

data class CodeAnalysisResponse(
    val overallScore: Int, // 0-100
    val issues: List<CodeIssue>,
    val suggestions: List<CodeSuggestion>,
    val metrics: CodeMetrics,
    val securityIssues: List<SecurityIssue> = emptyList(),
    val performanceIssues: List<PerformanceIssue> = emptyList()
)

data class CodeIssue(
    val type: String, // syntax, logic, style, security, performance
    val severity: String, // low, medium, high, critical
    val message: String,
    val line: Int? = null,
    val column: Int? = null,
    val suggestion: String? = null
)

data class CodeSuggestion(
    val type: String, // improvement, alternative, best-practice
    val message: String,
    val example: String? = null,
    val line: Int? = null
)

data class CodeMetrics(
    val linesOfCode: Int,
    val cyclomaticComplexity: Int,
    val maintainabilityIndex: Double,
    val duplicatedLines: Int,
    val testCoverage: Double? = null
)

data class SecurityIssue(
    val type: String, // injection, xss, csrf, etc.
    val severity: String,
    val description: String,
    val line: Int? = null,
    val recommendation: String
)

data class PerformanceIssue(
    val type: String, // algorithm, memory, io, etc.
    val impact: String, // low, medium, high
    val description: String,
    val line: Int? = null,
    val optimization: String
)

data class CodeCompletionResponse(
    val suggestions: List<CompletionSuggestion>,
    val context: String? = null
)

data class CompletionSuggestion(
    val text: String,
    val description: String? = null,
    val confidence: Double,
    val type: String // method, variable, class, keyword, etc.
)

data class CodeExplanationResponse(
    val explanation: String,
    val keyPoints: List<String>,
    val codeBreakdown: List<CodeSection>,
    val relatedConcepts: List<String> = emptyList()
)

data class CodeSection(
    val startLine: Int,
    val endLine: Int,
    val explanation: String,
    val purpose: String
)

data class DocumentationResponse(
    val documentation: String,
    val format: String, // markdown, html, plain
    val sections: List<DocSection>
)

data class DocSection(
    val title: String,
    val content: String,
    val type: String // overview, parameters, returns, examples, etc.
)

data class RefactoringResponse(
    val refactoredCode: String,
    val changes: List<RefactoringChange>,
    val explanation: String,
    val benefits: List<String>
)

data class RefactoringChange(
    val type: String, // rename, extract, inline, etc.
    val description: String,
    val originalLine: Int? = null,
    val newLine: Int? = null
)

data class CodeReviewResponse(
    val overallRating: String, // excellent, good, needs-improvement, poor
    val summary: String,
    val positiveAspects: List<String>,
    val improvementAreas: List<String>,
    val detailedFeedback: List<ReviewComment>
)

data class ReviewComment(
    val type: String, // praise, suggestion, issue, question
    val message: String,
    val line: Int? = null,
    val severity: String? = null // for issues: low, medium, high
)

data class BugFixResponse(
    val fixedCode: String,
    val explanation: String,
    val changes: List<BugFix>,
    val preventionTips: List<String>
)

data class BugFix(
    val description: String,
    val line: Int? = null,
    val originalCode: String? = null,
    val fixedCode: String? = null
)

data class CodeOptimizationResponse(
    val optimizedCode: String,
    val improvements: List<Optimization>,
    val performanceGain: String? = null,
    val explanation: String
)

data class Optimization(
    val type: String, // algorithm, memory, io, etc.
    val description: String,
    val impact: String, // low, medium, high
    val line: Int? = null
)

// Context DTOs for AI services
data class BotContext(
    val userId: Long,
    val snippetId: Long? = null,
    val repositoryId: Long? = null,
    val description: String? = null,
    val language: String? = null,
    val sessionId: String? = null
)

data class ConversationHistory(
    val messages: List<ConversationMessage>
)

data class ConversationMessage(
    val role: String, // user, assistant, system
    val content: String,
    val timestamp: LocalDateTime
)