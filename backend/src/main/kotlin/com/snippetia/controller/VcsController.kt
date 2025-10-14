package com.snippetia.controller

import com.snippetia.dto.*
import com.snippetia.service.VcsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/vcs")
@Tag(name = "Version Control System", description = "Git repository and version control operations")
@SecurityRequirement(name = "bearerAuth")
class VcsController(
    private val vcsService: VcsService
) {

    @GetMapping("/repositories")
    @Operation(summary = "Get user repositories", description = "Get all repositories for the authenticated user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Repositories retrieved successfully"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    ])
    fun getUserRepositories(
        authentication: Authentication,
        pageable: Pageable
    ): ResponseEntity<Page<RepositoryResponse>> {
        val username = authentication.name
        val repositories = vcsService.getUserRepositories(username, pageable)
        return ResponseEntity.ok(repositories)
    }

    @PostMapping("/repositories")
    @Operation(summary = "Create repository", description = "Create a new Git repository")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Repository created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid request"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    ])
    fun createRepository(
        @RequestBody request: CreateRepositoryRequest,
        authentication: Authentication
    ): ResponseEntity<RepositoryResponse> {
        val username = authentication.name
        val repository = vcsService.createRepository(username, request)
        return ResponseEntity.status(201).body(repository)
    }

    @GetMapping("/repositories/{repositoryId}")
    @Operation(summary = "Get repository", description = "Get repository details by ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Repository retrieved successfully"),
        ApiResponse(responseCode = "404", description = "Repository not found")
    ])
    fun getRepository(
        @Parameter(description = "Repository ID", required = true)
        @PathVariable repositoryId: Long
    ): ResponseEntity<RepositoryResponse> {
        val repository = vcsService.getRepository(repositoryId)
        return ResponseEntity.ok(repository)
    }

    @DeleteMapping("/repositories/{repositoryId}")
    @Operation(summary = "Delete repository", description = "Delete a repository")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Repository deleted successfully"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "403", description = "Forbidden"),
        ApiResponse(responseCode = "404", description = "Repository not found")
    ])
    fun deleteRepository(
        @Parameter(description = "Repository ID", required = true)
        @PathVariable repositoryId: Long,
        authentication: Authentication
    ): ResponseEntity<Void> {
        val username = authentication.name
        vcsService.deleteRepository(repositoryId, username)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/repositories/{repositoryId}/commits")
    @Operation(summary = "Get repository commits", description = "Get commits for a repository")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Commits retrieved successfully"),
        ApiResponse(responseCode = "404", description = "Repository not found")
    ])
    fun getRepositoryCommits(
        @Parameter(description = "Repository ID", required = true)
        @PathVariable repositoryId: Long,
        @Parameter(description = "Branch name")
        @RequestParam(defaultValue = "main") branch: String,
        pageable: Pageable
    ): ResponseEntity<Page<CommitResponse>> {
        val commits = vcsService.getRepositoryCommits(repositoryId, branch, pageable)
        return ResponseEntity.ok(commits)
    }

    @PostMapping("/repositories/{repositoryId}/commits")
    @Operation(summary = "Create commit", description = "Create a new commit in the repository")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Commit created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid request"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "404", description = "Repository not found")
    ])
    fun createCommit(
        @Parameter(description = "Repository ID", required = true)
        @PathVariable repositoryId: Long,
        @RequestBody request: CreateCommitRequest,
        authentication: Authentication
    ): ResponseEntity<CommitResponse> {
        val username = authentication.name
        val commit = vcsService.createCommit(repositoryId, request, username)
        return ResponseEntity.status(201).body(commit)
    }

    @GetMapping("/repositories/{repositoryId}/commits/{commitSha}")
    @Operation(summary = "Get commit", description = "Get commit details by SHA")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Commit retrieved successfully"),
        ApiResponse(responseCode = "404", description = "Commit not found")
    ])
    fun getCommit(
        @Parameter(description = "Repository ID", required = true)
        @PathVariable repositoryId: Long,
        @Parameter(description = "Commit SHA", required = true)
        @PathVariable commitSha: String
    ): ResponseEntity<CommitResponse> {
        val commit = vcsService.getCommit(repositoryId, commitSha)
        return ResponseEntity.ok(commit)
    }

    @GetMapping("/repositories/{repositoryId}/branches")
    @Operation(summary = "Get repository branches", description = "Get all branches for a repository")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Branches retrieved successfully"),
        ApiResponse(responseCode = "404", description = "Repository not found")
    ])
    fun getRepositoryBranches(
        @Parameter(description = "Repository ID", required = true)
        @PathVariable repositoryId: Long
    ): ResponseEntity<List<BranchResponse>> {
        val branches = vcsService.getRepositoryBranches(repositoryId)
        return ResponseEntity.ok(branches)
    }

    @PostMapping("/repositories/{repositoryId}/branches")
    @Operation(summary = "Create branch", description = "Create a new branch in the repository")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Branch created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid request"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "404", description = "Repository not found")
    ])
    fun createBranch(
        @Parameter(description = "Repository ID", required = true)
        @PathVariable repositoryId: Long,
        @RequestBody request: CreateBranchRequest,
        authentication: Authentication
    ): ResponseEntity<BranchResponse> {
        val username = authentication.name
        val branch = vcsService.createBranch(repositoryId, request, username)
        return ResponseEntity.status(201).body(branch)
    }

    @PostMapping("/repositories/{repositoryId}/sync")
    @Operation(summary = "Sync repository", description = "Sync repository with external Git provider")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Repository synced successfully"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "404", description = "Repository not found")
    ])
    fun syncRepository(
        @Parameter(description = "Repository ID", required = true)
        @PathVariable repositoryId: Long,
        authentication: Authentication
    ): ResponseEntity<Map<String, String>> {
        val username = authentication.name
        vcsService.syncRepository(repositoryId, username)
        return ResponseEntity.ok(mapOf("status" to "synced"))
    }
}