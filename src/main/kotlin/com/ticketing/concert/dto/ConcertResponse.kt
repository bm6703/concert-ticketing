package com.ticketing.concert.dto

import com.ticketing.concert.domain.Concert
import com.ticketing.concert.domain.ConcertStatus
import java.io.Serializable
import java.time.LocalDateTime

data class ConcertResponse(
    val id: Long,
    val title: String,
    val venue: String,
    val performanceAt: LocalDateTime,
    val bookingOpenAt: LocalDateTime,
    val status: ConcertStatus
) : Serializable {
    companion object {
        fun from(concert: Concert): ConcertResponse {
            return ConcertResponse(
                id = concert.id!!,
                title = concert.title,
                venue = concert.venue,
                performanceAt = concert.performanceAt,
                bookingOpenAt = concert.bookingOpenAt,
                status = concert.status
            )
        }
    }
}