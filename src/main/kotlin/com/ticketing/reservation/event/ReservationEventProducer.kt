package com.ticketing.reservation.event

import com.ticketing.reservation.dto.ReservationResponse
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class ReservationEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val jsonMapper: JsonMapper
) {

    fun publishReservationConfirmed(response: ReservationResponse) {
        val event = ReservationConfirmedEvent(
            reservationId = response.id,
            memberId = response.memberId,
            ticketTypeId = response.ticketTypeId,
            quantity = response.quantity
        )
        val json = jsonMapper.writeValueAsString(event)
        kafkaTemplate.send("reservation-confirmed", json)
    }
}