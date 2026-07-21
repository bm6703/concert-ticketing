package com.ticketing.concert.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class ConcertCreateRequest(

    @field:NotBlank
    val title: String,

    @field:NotBlank
    val venue: String,

    @field:NotNull
    val performanceAt: LocalDateTime,

    @field:NotNull
    val bookingOpenAt: LocalDateTime

)