package com.ticketing.reservation.event

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class ReservationNotificationConsumer(
    private val jsonMapper: JsonMapper
) {

    @KafkaListener(topics = ["reservation-confirmed"], groupId = "notification-group")
    fun consume(message: String) {
        val event = jsonMapper.readValue(message, ReservationConfirmedEvent::class.java)
        println("[알림] 회원 ${event.memberId}님, 예약(id=${event.reservationId})이 확정되었습니다. (티켓타입=${event.ticketTypeId}, 수량=${event.quantity})")
    }
}