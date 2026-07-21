package com.ticketing.tickettype.service

import com.ticketing.concert.repository.ConcertRepository
import com.ticketing.global.exception.NotFoundException
import com.ticketing.tickettype.domain.TicketType
import com.ticketing.tickettype.dto.TicketTypeCreateRequest
import com.ticketing.tickettype.dto.TicketTypeResponse
import com.ticketing.tickettype.repository.TicketTypeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TicketTypeService(
    private val ticketTypeRepository: TicketTypeRepository,
    private val concertRepository: ConcertRepository
) {

    @Transactional
    fun createTicketType(concertId: Long, request: TicketTypeCreateRequest): TicketTypeResponse {
        val concert = concertRepository.findById(concertId)
            .orElseThrow { NotFoundException("공연을 찾을 수 없습니다. id=$concertId") }

        val ticketType = TicketType(
            concert = concert,
            gradeName = request.gradeName,
            price = request.price,
            totalQuantity = request.totalQuantity,
            remainingQuantity = request.totalQuantity
        )
        val saved = ticketTypeRepository.save(ticketType)
        return TicketTypeResponse.from(saved)
    }
}