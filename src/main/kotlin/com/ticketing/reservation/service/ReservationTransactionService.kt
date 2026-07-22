package com.ticketing.reservation.service

import com.ticketing.global.exception.NotFoundException
import com.ticketing.member.repository.MemberRepository
import com.ticketing.reservation.domain.Reservation
import com.ticketing.reservation.dto.ReservationCreateRequest
import com.ticketing.reservation.dto.ReservationResponse
import com.ticketing.reservation.repository.ReservationRepository
import com.ticketing.tickettype.repository.TicketTypeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReservationTransactionService(
    private val reservationRepository: ReservationRepository,
    private val ticketTypeRepository: TicketTypeRepository,
    private val memberRepository: MemberRepository
) {

    @Transactional
    fun reserve(request: ReservationCreateRequest): ReservationResponse {
        val member = memberRepository.findById(request.memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다. id=${request.memberId}") }

        val ticketType = ticketTypeRepository.findById(request.ticketTypeId)
            .orElseThrow { NotFoundException("좌석 등급을 찾을 수 없습니다. id=${request.ticketTypeId}") }

        check(ticketType.remainingQuantity >= request.quantity) {
            "잔여 좌석이 부족합니다. 남은 수량=${ticketType.remainingQuantity}"
        }

        ticketType.remainingQuantity -= request.quantity

        val reservation = Reservation(
            member = member,
            ticketType = ticketType,
            quantity = request.quantity
        )
        val saved = reservationRepository.save(reservation)
        return ReservationResponse.from(saved)
    }
}