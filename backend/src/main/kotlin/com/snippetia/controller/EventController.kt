package com.snippetia.controller

import com.snippetia.dto.*
import com.snippetia.service.EventService
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
@RequestMapping("/api/v1/events")
@Tag(name = "Events", description = "Developer events and meetups management")
@SecurityRequirement(name = "bearerAuth")
class EventController(
    private val eventService: EventService
) {

    @GetMapping
    @Operation(summary = "Get all events", description = "Get paginated list of all public events")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    ])
    fun getAllEvents(
        @Parameter(description = "Event type filter")
        @RequestParam(required = false) type: String?,
        @Parameter(description = "Channel ID filter")
        @RequestParam(required = false) channelId: Long?,
        pageable: Pageable
    ): ResponseEntity<Page<EventResponse>> {
        val events = eventService.getAllEvents(type, channelId, pageable)
        return ResponseEntity.ok(events)
    }

    @PostMapping
    @Operation(summary = "Create event", description = "Create a new developer event")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Event created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid request"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    ])
    fun createEvent(
        @RequestBody request: CreateEventRequest,
        authentication: Authentication
    ): ResponseEntity<EventResponse> {
        val username = authentication.name
        val event = eventService.createEvent(request, username)
        return ResponseEntity.status(201).body(event)
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Get event", description = "Get event details by ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Event retrieved successfully"),
        ApiResponse(responseCode = "404", description = "Event not found")
    ])
    fun getEvent(
        @Parameter(description = "Event ID", required = true)
        @PathVariable eventId: Long
    ): ResponseEntity<EventResponse> {
        val event = eventService.getEvent(eventId)
        return ResponseEntity.ok(event)
    }

    @PutMapping("/{eventId}")
    @Operation(summary = "Update event", description = "Update an existing event")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Event updated successfully"),
        ApiResponse(responseCode = "400", description = "Invalid request"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "403", description = "Forbidden"),
        ApiResponse(responseCode = "404", description = "Event not found")
    ])
    fun updateEvent(
        @Parameter(description = "Event ID", required = true)
        @PathVariable eventId: Long,
        @RequestBody request: UpdateEventRequest,
        authentication: Authentication
    ): ResponseEntity<EventResponse> {
        val username = authentication.name
        val event = eventService.updateEvent(eventId, request, username)
        return ResponseEntity.ok(event)
    }

    @DeleteMapping("/{eventId}")
    @Operation(summary = "Delete event", description = "Delete an event")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Event deleted successfully"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "403", description = "Forbidden"),
        ApiResponse(responseCode = "404", description = "Event not found")
    ])
    fun deleteEvent(
        @Parameter(description = "Event ID", required = true)
        @PathVariable eventId: Long,
        authentication: Authentication
    ): ResponseEntity<Void> {
        val username = authentication.name
        eventService.deleteEvent(eventId, username)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{eventId}/attend")
    @Operation(summary = "Attend event", description = "Register attendance for an event")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Attendance registered successfully"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "404", description = "Event not found")
    ])
    fun attendEvent(
        @Parameter(description = "Event ID", required = true)
        @PathVariable eventId: Long,
        authentication: Authentication
    ): ResponseEntity<Map<String, String>> {
        val username = authentication.name
        eventService.attendEvent(eventId, username)
        return ResponseEntity.ok(mapOf("status" to "attending"))
    }

    @DeleteMapping("/{eventId}/attend")
    @Operation(summary = "Cancel attendance", description = "Cancel attendance for an event")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Attendance cancelled successfully"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "404", description = "Event not found")
    ])
    fun cancelAttendance(
        @Parameter(description = "Event ID", required = true)
        @PathVariable eventId: Long,
        authentication: Authentication
    ): ResponseEntity<Map<String, String>> {
        val username = authentication.name
        eventService.cancelAttendance(eventId, username)
        return ResponseEntity.ok(mapOf("status" to "cancelled"))
    }

    @GetMapping("/{eventId}/attendees")
    @Operation(summary = "Get event attendees", description = "Get list of event attendees")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Attendees retrieved successfully"),
        ApiResponse(responseCode = "404", description = "Event not found")
    ])
    fun getEventAttendees(
        @Parameter(description = "Event ID", required = true)
        @PathVariable eventId: Long,
        pageable: Pageable
    ): ResponseEntity<Page<UserSummaryResponse>> {
        val attendees = eventService.getEventAttendees(eventId, pageable)
        return ResponseEntity.ok(attendees)
    }

    @GetMapping("/my")
    @Operation(summary = "Get my events", description = "Get events organized by the current user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    ])
    fun getMyEvents(
        authentication: Authentication,
        pageable: Pageable
    ): ResponseEntity<Page<EventResponse>> {
        val username = authentication.name
        val events = eventService.getUserEvents(username, pageable)
        return ResponseEntity.ok(events)
    }

    @GetMapping("/attending")
    @Operation(summary = "Get attending events", description = "Get events the current user is attending")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    ])
    fun getAttendingEvents(
        authentication: Authentication,
        pageable: Pageable
    ): ResponseEntity<Page<EventResponse>> {
        val username = authentication.name
        val events = eventService.getAttendingEvents(username, pageable)
        return ResponseEntity.ok(events)
    }
}