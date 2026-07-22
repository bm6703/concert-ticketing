package com.ticketing.concert.service

import com.ticketing.concert.domain.Concert
import com.ticketing.concert.dto.ConcertCreateRequest
import com.ticketing.concert.dto.ConcertResponse
import com.ticketing.concert.repository.ConcertRepository
import com.ticketing.global.exception.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.cache.annotation.Cacheable

@Service
class ConcertService(
    private val concertRepository: ConcertRepository
) {

    @Transactional
    fun createConcert(request: ConcertCreateRequest): ConcertResponse {
        val concert = Concert(
            title = request.title,
            venue = request.venue,
            performanceAt = request.performanceAt,
            bookingOpenAt = request.bookingOpenAt
        )
        val saved = concertRepository.save(concert)
        return ConcertResponse.from(saved)
    }

    @org.springframework.cache.annotation.Cacheable(value = ["concert"], key = "#id")
    @Transactional(readOnly = true)
    fun getConcert(id: Long): ConcertResponse {
        val concert = concertRepository.findById(id)
            .orElseThrow { NotFoundException("공연을 찾을 수 없습니다. id=$id") }
        return ConcertResponse.from(concert)
    }
}