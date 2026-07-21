package com.ticketing.tickettype.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class TicketTypeCreateRequest(

    @field:NotBlank
    val gradeName: String,

    @field:NotNull
    @field:Positive
    val price: Int,

    @field:NotNull
    @field:Positive
    val totalQuantity: Int

)