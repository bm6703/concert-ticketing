package com.ticketing.reservation.dto

import com.ticketing.reservation.domain.Reservation
import com.ticketing.reservation.domain.ReservationStatus
import java.time.LocalDateTime

data class ReservationResponse(
    val id: Long,
    val memberId: Long,
    val ticketTypeId: Long,
    val quantity: Int,
    val status: ReservationStatus,
    val reservedAt: LocalDateTime
) {
    companion object {
        fun from(reservation: Reservation): ReservationResponse {
            return ReservationResponse(
                id = reservation.id!!,
                memberId = reservation.member.id!!,
                ticketTypeId = reservation.ticketType.id!!,
                quantity = reservation.quantity,
                status = reservation.status,
                reservedAt = reservation.reservedAt
            )
        }
    }
}