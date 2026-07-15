package com.ticketing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ConcertTicketingApplication

fun main(args: Array<String>) {
	runApplication<ConcertTicketingApplication>(*args)
}
