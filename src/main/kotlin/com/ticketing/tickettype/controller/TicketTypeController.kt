package com.ticketing.tickettype.controller

import com.ticketing.tickettype.dto.TicketTypeCreateRequest
import com.ticketing.tickettype.dto.TicketTypeResponse
import com.ticketing.tickettype.service.TicketTypeService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/concerts/{concertId}/ticket-types")
class TicketTypeController(
    private val ticketTypeService: TicketTypeService
) {

    @PostMapping
    fun createTicketType(
        @PathVariable concertId: Long,
        @Valid @RequestBody request: TicketTypeCreateRequest
    ): ResponseEntity<TicketTypeResponse> {
        val response = ticketTypeService.createTicketType(concertId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}