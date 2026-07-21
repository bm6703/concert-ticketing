package com.ticketing.reservation.controller

import com.ticketing.reservation.dto.ReservationCreateRequest
import com.ticketing.reservation.dto.ReservationResponse
import com.ticketing.reservation.service.ReservationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/reservations")
class ReservationController(
    private val reservationService: ReservationService
) {

    @PostMapping
    fun createReservation(@Valid @RequestBody request: ReservationCreateRequest): ResponseEntity<ReservationResponse> {
        val response = reservationService.createReservation(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getReservation(@PathVariable id: Long): ResponseEntity<ReservationResponse> {
        val response = reservationService.getReservation(id)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun cancelReservation(@PathVariable id: Long): ResponseEntity<Void> {
        reservationService.cancelReservation(id)
        return ResponseEntity.noContent().build()
    }
}