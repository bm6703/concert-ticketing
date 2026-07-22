package com.ticketing.reservation

import com.ticketing.concert.dto.ConcertCreateRequest
import com.ticketing.concert.service.ConcertService
import com.ticketing.member.domain.Member
import com.ticketing.member.repository.MemberRepository
import com.ticketing.reservation.dto.ReservationCreateRequest
import com.ticketing.reservation.service.ReservationService
import com.ticketing.tickettype.dto.TicketTypeCreateRequest
import com.ticketing.tickettype.repository.TicketTypeRepository
import com.ticketing.tickettype.service.TicketTypeService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SpringBootTest
class ReservationConcurrencyTest {

    @Autowired lateinit var reservationService: ReservationService
    @Autowired lateinit var concertService: ConcertService
    @Autowired lateinit var ticketTypeService: TicketTypeService
    @Autowired lateinit var ticketTypeRepository: TicketTypeRepository
    @Autowired lateinit var memberRepository: MemberRepository

    @Test
    fun `동시에 여러 명이 예약해도 재고가 마이너스로 내려가지 않는다`() {
        // given: 이 테스트 안에서 직접 회원 + 콘서트 + 재고 5개짜리 티켓타입 준비
        val member = memberRepository.save(
            Member(email = "concurrency-${System.nanoTime()}@test.com", name = "동시성테스트유저")
        )

        val concert = concertService.createConcert(
            ConcertCreateRequest(
                title = "동시성테스트콘서트",
                venue = "테스트공연장",
                performanceAt = LocalDateTime.now().plusDays(30),
                bookingOpenAt = LocalDateTime.now()
            )
        )

        val ticketType = ticketTypeService.createTicketType(
            concert.id,
            TicketTypeCreateRequest(gradeName = "TEST", price = 10000, totalQuantity = 5)
        )

        val threadCount = 10
        val executorService = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)

        // when: 스레드 10개가 동시에 예약 시도 (재고는 5개뿐)
        repeat(threadCount) {
            executorService.submit {
                try {
                    reservationService.createReservation(
                        ReservationCreateRequest(
                            memberId = member.id!!,
                            ticketTypeId = ticketType.id,
                            quantity = 1
                        )
                    )
                } catch (e: Exception) {
                    // 재고 부족으로 실패하는 것도 정상 동작
                } finally {
                    latch.countDown()
                }
            }
        }
        latch.await(10, TimeUnit.SECONDS)
        executorService.shutdown()

        // then: 재고는 절대 마이너스가 되면 안 되고, 정확히 0으로 마감돼야 함
        val result = ticketTypeRepository.findById(ticketType.id).get()
        println("최종 남은 재고: ${result.remainingQuantity}")
        assertEquals(0, result.remainingQuantity)
    }
}