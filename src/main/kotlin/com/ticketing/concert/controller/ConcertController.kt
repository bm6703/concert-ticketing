package com.ticketing.concert.controller

import com.ticketing.concert.dto.ConcertCreateRequest
import com.ticketing.concert.dto.ConcertResponse
import com.ticketing.concert.service.ConcertService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/concerts")
class ConcertController(
    private val concertService: ConcertService
) {

    @PostMapping
    fun createConcert(@Valid @RequestBody request: ConcertCreateRequest): ResponseEntity<ConcertResponse> {
        val response = concertService.createConcert(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getConcert(@PathVariable id: Long): ResponseEntity<ConcertResponse> {
        val response = concertService.getConcert(id)
        return ResponseEntity.ok(response)
    }
}