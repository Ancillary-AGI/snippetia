package com.snippetia.controller

import com.snippetia.dto.*
import com.snippetia.service.ChannelService
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
@RequestMapping("/api/v1/channels")
@Tag(name = "Channels", description = "Developer channels and content management")
@SecurityRequirement(name = "bearerAuth")
class ChannelController(
    private val channelService: ChannelService
) {

    @GetMapping
    @Operation(summary = "Get all channels", description = "Get paginated list of all public channels")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Channels retrieved successfully")
    ])
    fun getAllChannels(
        @Parameter(description = "Search query")
        @RequestParam(required = false) search: String?,
        @Parameter(description = "Category filter")
        @RequestParam(required = false) category: String?,
        pageable: Pageable
    ): ResponseEntity<Page<ChannelResponse>> {
        val channels = channelService.getAllChannels(search, category, pageable)
        return ResponseEntity.ok(channels)
    }

    @PostMapping
    @Operation(summary = "Create channel", description = "Create a new developer channel")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Channel created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid request"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    ])
    fun createChannel(
        @RequestBody request: CreateChannelRequest,
        authentication: Authentication
    ): ResponseEntity<ChannelResponse> {
        val username = authentication.name
        val channel = channelService.createChannel(request, username)
        return ResponseEntity.status(201).body(channel)
    }

    @GetMapping("/{channelId}")
    @Operation(summary = "Get channel", description = "Get channel details by ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Channel retrieved successfully"),
        ApiResponse(responseCode = "404", description = "Channel not found")
    ])
    fun getChannel(
        @Parameter(description = "Channel ID", required = true)
        @PathVariable channelId: Long
    ): ResponseEntity<ChannelResponse> {
        val channel = channelService.getChannel(channelId)
        return ResponseEntity.ok(channel)
    }

    @PutMapping("/{channelId}")
    @Operation(summary = "Update channel", description = "Update an existing channel")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Channel updated successfully"),
        ApiResponse(responseCode = "400", description = "Invalid request"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "403", description = "Forbidden"),
        ApiResponse(responseCode = "404", description = "Channel not found")
    ])
    fun updateChannel(
        @Parameter(description = "Channel ID", required = true)
        @PathVariable channelId: Long,
        @RequestBody request: UpdateChannelRequest,
        authentication: Authentication
    ): ResponseEntity<ChannelResponse> {
        val username = authentication.name
        val channel = channelService.updateChannel(channelId, request, username)
        return ResponseEntity.ok(channel)
    }

    @DeleteMapping("/{channelId}")
    @Operation(summary = "Delete channel", description = "Delete a channel")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Channel deleted successfully"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "403", description = "Forbidden"),
        ApiResponse(responseCode = "404", description = "Channel not found")
    ])
    fun deleteChannel(
        @Parameter(description = "Channel ID", required = true)
        @PathVariable channelId: Long,
        authentication: Authentication
    ): ResponseEntity<Void> {
        val username = authentication.name
        channelService.deleteChannel(channelId, username)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{channelId}/snippets")
    @Operation(summary = "Get channel snippets", description = "Get all snippets in a channel")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Snippets retrieved successfully"),
        ApiResponse(responseCode = "404", description = "Channel not found")
    ])
    fun getChannelSnippets(
        @Parameter(description = "Channel ID", required = true)
        @PathVariable channelId: Long,
        pageable: Pageable
    ): ResponseEntity<Page<SnippetResponse>> {
        val snippets = channelService.getChannelSnippets(channelId, pageable)
        return ResponseEntity.ok(snippets)
    }

    @PostMapping("/{channelId}/snippets/{snippetId}")
    @Operation(summary = "Add snippet to channel", description = "Add a snippet to a channel")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Snippet added to channel successfully"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "403", description = "Forbidden"),
        ApiResponse(responseCode = "404", description = "Channel or snippet not found")
    ])
    fun addSnippetToChannel(
        @Parameter(description = "Channel ID", required = true)
        @PathVariable channelId: Long,
        @Parameter(description = "Snippet ID", required = true)
        @PathVariable snippetId: Long,
        authentication: Authentication
    ): ResponseEntity<Map<String, String>> {
        val username = authentication.name
        channelService.addSnippetToChannel(channelId, snippetId, username)
        return ResponseEntity.ok(mapOf("status" to "added"))
    }

    @DeleteMapping("/{channelId}/snippets/{snippetId}")
    @Operation(summary = "Remove snippet from channel", description = "Remove a snippet from a channel")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Snippet removed from channel successfully"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "403", description = "Forbidden"),
        ApiResponse(responseCode = "404", description = "Channel or snippet not found")
    ])
    fun removeSnippetFromChannel(
        @Parameter(description = "Channel ID", required = true)
        @PathVariable channelId: Long,
        @Parameter(description = "Snippet ID", required = true)
        @PathVariable snippetId: Long,
        authentication: Authentication
    ): ResponseEntity<Map<String, String>> {
        val username = authentication.name
        channelService.removeSnippetFromChannel(channelId, snippetId, username)
        return ResponseEntity.ok(mapOf("status" to "removed"))
    }

    @GetMapping("/my")
    @Operation(summary = "Get my channels", description = "Get channels owned by the current user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Channels retrieved successfully"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    ])
    fun getMyChannels(
        authentication: Authentication,
        pageable: Pageable
    ): ResponseEntity<Page<ChannelResponse>> {
        val username = authentication.name
        val channels = channelService.getUserChannels(username, pageable)
        return ResponseEntity.ok(channels)
    }

    @GetMapping("/subscribed")
    @Operation(summary = "Get subscribed channels", description = "Get channels the current user is subscribed to")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Channels retrieved successfully"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    ])
    fun getSubscribedChannels(
        authentication: Authentication,
        pageable: Pageable
    ): ResponseEntity<Page<ChannelResponse>> {
        val username = authentication.name
        val channels = channelService.getSubscribedChannels(username, pageable)
        return ResponseEntity.ok(channels)
    }

    @GetMapping("/trending")
    @Operation(summary = "Get trending channels", description = "Get trending channels based on activity")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Trending channels retrieved successfully")
    ])
    fun getTrendingChannels(
        @Parameter(description = "Time period (day, week, month)")
        @RequestParam(defaultValue = "week") period: String,
        pageable: Pageable
    ): ResponseEntity<Page<ChannelResponse>> {
        val channels = channelService.getTrendingChannels(period, pageable)
        return ResponseEntity.ok(channels)
    }
}