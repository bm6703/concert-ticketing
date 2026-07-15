package com.ticketing.tickettype.repository

import com.ticketing.tickettype.domain.TicketType
import org.springframework.data.jpa.repository.JpaRepository

interface TicketTypeRepository : JpaRepository<TicketType, Long>