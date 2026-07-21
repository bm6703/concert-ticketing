package com.ticketing.reservation.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class ReservationCreateRequest(

    @field:NotNull
    val memberId: Long,

    @field:NotNull
    val ticketTypeId: Long,

    @field:NotNull
    @field:Positive
    val quantity: Int

)