package com.ticketing.tickettype.dto

import com.ticketing.tickettype.domain.TicketType

data class TicketTypeResponse(
    val id: Long,
    val concertId: Long,
    val gradeName: String,
    val price: Int,
    val totalQuantity: Int,
    val remainingQuantity: Int
) {
    companion object {
        fun from(ticketType: TicketType): TicketTypeResponse {
            return TicketTypeResponse(
                id = ticketType.id!!,
                concertId = ticketType.concert.id!!,
                gradeName = ticketType.gradeName,
                price = ticketType.price,
                totalQuantity = ticketType.totalQuantity,
                remainingQuantity = ticketType.remainingQuantity
            )
        }
    }
}