package com.ticketing.reservation.repository

import com.ticketing.reservation.domain.Reservation
import org.springframework.data.jpa.repository.JpaRepository

interface ReservationRepository : JpaRepository<Reservation, Long>