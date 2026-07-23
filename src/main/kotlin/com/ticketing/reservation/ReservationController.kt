package com.ticketing.reservation.controller

import com.ticketing.reservation.dto.ReservationCreateRequest
import com.ticketing.reservation.dto.ReservationResponse
import com.ticketing.reservation.service.ReservationService
import jakarta.validation.Valid
import org.redisson.api.RedissonClient
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/reservations")
class ReservationController(
    private val reservationService: ReservationService,
    private val redissonClient: RedissonClient
) {

    @PostMapping
    fun createReservation(
        @RequestHeader(value = "Idempotency-Key", required = false) idempotencyKey: String?,
        @Valid @RequestBody request: ReservationCreateRequest
    ): ResponseEntity<Any> {

        // Idempotency-Key를 안 보내는 클라이언트도 있을 수 있으니, 없으면 기존 로직 그대로 동작
        if (idempotencyKey == null) {
            val response = reservationService.createReservation(request)
            return ResponseEntity.status(HttpStatus.CREATED).body(response)
        }

        val bucket = redissonClient.getBucket<Any>("idempotency:$idempotencyKey")

        // 1) 이미 처리된 적 있는 키인지 확인
        val cached = bucket.get()
        if (cached != null) {
            if (cached == "PROCESSING") {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 처리 중인 요청입니다.")
            }
            val cachedReservationId = (cached as Number).toLong()
            val response = reservationService.getReservation(cachedReservationId)
            return ResponseEntity.status(HttpStatus.OK).body(response)
        }

        // 2) 처음 보는 키 → "처리중" 마커를 원자적으로 선점 (동시에 같은 키로 와도 하나만 통과)
        val acquired = bucket.trySet("PROCESSING", 60, TimeUnit.SECONDS)
        if (!acquired) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 처리 중인 요청입니다.")
        }

        // 3) 실제 예약 처리, 결과(id)를 같은 키에 저장 (24시간 보관 → 재시도 대응)
        return try {
            val response = reservationService.createReservation(request)
            bucket.set(response.id, 24, TimeUnit.HOURS)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: Exception) {
            bucket.delete() // 실패했으면 키 해제 → 나중에 재시도 가능하게
            throw e
        }
    }

    @GetMapping("/{id}")
    fun getReservation(@PathVariable id: Long): ResponseEntity<ReservationResponse> {
        val response = reservationService.getReservation(id)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun cancelReservation(@PathVariable id: Long): ResponseEntity<Void> {
        reservationService.cancelReservation(id)
        return ResponseEntity.noContent().build()
    }
}