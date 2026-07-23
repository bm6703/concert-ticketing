package com.ticketing.reservation.event

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class ReservationStatsConsumer(
    private val jsonMapper: JsonMapper
) {

    @KafkaListener(topics = ["reservation-confirmed"], groupId = "stats-group")
    fun consume(message: String) {
        val event = jsonMapper.readValue(message, ReservationConfirmedEvent::class.java)
        println("[통계] 티켓타입 ${event.ticketTypeId} 판매 통계 갱신: 이번 예약 수량 ${event.quantity}건 반영")
    }
}