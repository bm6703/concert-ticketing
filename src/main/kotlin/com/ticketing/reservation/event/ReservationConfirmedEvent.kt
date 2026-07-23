package com.ticketing.reservation.event

data class ReservationConfirmedEvent(
    val reservationId: Long,
    val memberId: Long,
    val ticketTypeId: Long,
    val quantity: Int
)