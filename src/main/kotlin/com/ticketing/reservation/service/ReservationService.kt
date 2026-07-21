package com.ticketing.reservation.service

import com.ticketing.global.exception.NotFoundException
import com.ticketing.member.repository.MemberRepository
import com.ticketing.reservation.domain.Reservation
import com.ticketing.reservation.domain.ReservationStatus
import com.ticketing.reservation.dto.ReservationCreateRequest
import com.ticketing.reservation.dto.ReservationResponse
import com.ticketing.reservation.repository.ReservationRepository
import com.ticketing.tickettype.repository.TicketTypeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val ticketTypeRepository: TicketTypeRepository,
    private val memberRepository: MemberRepository
) {

    @Transactional
    fun createReservation(request: ReservationCreateRequest): ReservationResponse {
        val member = memberRepository.findById(request.memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다. id=${request.memberId}") }

        val ticketType = ticketTypeRepository.findById(request.ticketTypeId)
            .orElseThrow { NotFoundException("좌석 등급을 찾을 수 없습니다. id=${request.ticketTypeId}") }

        check(ticketType.remainingQuantity >= request.quantity) {
            "잔여 좌석이 부족합니다. 남은 수량=${ticketType.remainingQuantity}"
        }

        // 주의: 지금은 일부러 이렇게 단순하게 차감함 (동시 요청 처리 X)
        // 이게 1주차 "오버셀링 재현" 포인트 - 3주차에 Redis 분산락으로 해결 예정
        ticketType.remainingQuantity -= request.quantity

        val reservation = Reservation(
            member = member,
            ticketType = ticketType,
            quantity = request.quantity
        )
        val saved = reservationRepository.save(reservation)
        return ReservationResponse.from(saved)
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