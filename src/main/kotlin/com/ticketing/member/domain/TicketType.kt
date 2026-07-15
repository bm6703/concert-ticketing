package com.ticketing.tickettype.domain

import com.ticketing.concert.domain.Concert
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "ticket_type")
class TicketType(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    val concert: Concert,

    @Column(nullable = false)
    val gradeName: String,

    @Column(nullable = false)
    val price: Int,

    @Column(nullable = false)
    val totalQuantity: Int,

    @Column(nullable = false)
    var remainingQuantity: Int

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set
}