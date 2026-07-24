# 아키텍처 설계

## ERD

```
MEMBER ||--o{ RESERVATION : makes
CONCERT ||--o{ TICKET_TYPE : has
TICKET_TYPE ||--o{ RESERVATION : reserved_as

MEMBER {
  bigint id PK
  string email
  string name
}
CONCERT {
  bigint id PK
  string title
  string venue
  datetime performance_at
  datetime booking_open_at
  string status
}
TICKET_TYPE {
  bigint id PK
  bigint concert_id FK
  string grade_name
  int price
  int total_quantity
  int remaining_quantity
}
RESERVATION {
  bigint id PK
  bigint member_id FK
  bigint ticket_type_id FK
  int quantity
  string status
  datetime reserved_at
}
```

설계 포인트: 좌석을 개별 지정석이 아니라 **등급 단위 재고**(TICKET_TYPE.remaining_quantity)로 모델링.
이 필드가 1주차 오버셀링 재현과 3주차 Redis 분산락 해결의 핵심 대상.

## 패키지 구조

```
com.ticketing
├── concert
│   ├── controller
│   ├── service
│   ├── repository
│   └── domain
├── tickettype
│   ├── controller
│   ├── service      # 재고 증감 로직 위치 → 3주차 분산락 적용 지점
│   ├── repository
│   └── domain
├── reservation
│   ├── controller
│   ├── service       # 예약 생성/취소, 재고 차감 호출
│   ├── repository
│   └── domain
├── member
│   └── ...
└── global
    ├── config
    ├── exception
    └── common
```

## API 설계 (1주차 기준)

| Method | Endpoint | 설명 |
|---|---|---|
| POST | /concerts | 공연 등록 |
| GET | /concerts/{id} | 공연 상세 조회 |
| POST | /concerts/{id}/ticket-types | 좌석 등급/수량 등록 |
| POST | /reservations | 예약 생성 (ticketTypeId, quantity) |
| GET | /reservations/{id} | 예약 조회 |
| DELETE | /reservations/{id} | 예약 취소 |

## 동시성 제어 전략 (3주차)
- Redis 분산락(예: Redisson)으로 `TICKET_TYPE.remaining_quantity` 차감 임계 구역 보호
- 캐시: 공연 상세 조회는 Redis에 캐싱, TTL 및 무효화 정책은 예약 상태 변경 시 갱신

## 배치 / 부하테스트 (5주차)
- Spring Batch: 잔여수량/총수량 비율 ≤10%인 TicketType을 조회해 "매진 임박" 로그를 남기는 배치 (`POST /batch/near-sellout`으로 수동 트리거)
- k6: `GET /concerts/{id}` 캐시 적용 전후 TPS/응답시간 비교, `POST /reservations` 1000 VUs 동시 요청으로 분산락의 오버셀링 방지 검증
- 부하테스트 중 발견한 "응답 유실" 문제 대응으로 `POST /reservations`에 `Idempotency-Key` 헤더 지원 추가 (Redisson `RBucket`으로 중복 요청 방지)
