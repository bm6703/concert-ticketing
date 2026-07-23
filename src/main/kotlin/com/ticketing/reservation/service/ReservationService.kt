package com.ticketing.reservation.service

import com.ticketing.global.exception.NotFoundException
import com.ticketing.reservation.domain.ReservationStatus
import com.ticketing.reservation.dto.ReservationCreateRequest
import com.ticketing.reservation.dto.ReservationResponse
import com.ticketing.reservation.event.ReservationEventProducer
import com.ticketing.reservation.repository.ReservationRepository
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val reservationTransactionService: ReservationTransactionService,
    private val redissonClient: RedissonClient,
    private val reservationEventProducer: ReservationEventProducer
) {

    fun createReservation(request: ReservationCreateRequest): ReservationResponse {
        val lock = redissonClient.getLock("lock:ticketType:${request.ticketTypeId}")

        val acquired = lock.tryLock(5, 3, TimeUnit.SECONDS)
        if (!acquired) {
            throw IllegalStateException("요청이 몰리고 있습니다. 잠시 후 다시 시도해주세요.")
        }

        val response = try {
            reservationTransactionService.reserve(request)
        } finally {
            lock.unlock()
        }

        reservationEventProducer.publishReservationConfirmed(response)
        return response
    }

    @Transactional(readOnly = true)
    fun getReservation(id: Long): ReservationResponse {
        val reservation = reservationRepository.findById(id)
            .orElseThrow { NotFoundException("예약을 찾을 수 없습니다. id=$id") }
        return ReservationResponse.from(reservation)
    }

    @Transactional
    fun cancelReservation(id: Long) {
        val reservation = reservationRepository.findById(id)
            .orElseThrow { NotFoundException("예약을 찾을 수 없습니다. id=$id") }

        reservation.status = ReservationStatus.CANCELLED
        reservation.ticketType.remainingQuantity += reservation.quantity
    }
}