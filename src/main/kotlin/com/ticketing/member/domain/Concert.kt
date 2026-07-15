package com.ticketing.concert.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "concert")
class Concert(

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val venue: String,

    @Column(nullable = false)
    val performanceAt: LocalDateTime,

    @Column(nullable = false)
    val bookingOpenAt: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: ConcertStatus = ConcertStatus.SCHEDULED

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set
}

enum class ConcertStatus {
    SCHEDULED,   // 공연 등록됨, 예매 오픈 전
    OPEN,        // 예매 진행 중
    CLOSED       // 예매 종료 (매진 또는 공연 종료)
}