# 콘서트 티켓팅 예약 API

## 서비스 컨셉
콘서트/공연 정보를 등록하고, 사용자가 좌석 등급(VIP/R/S)별 수량을 예약하는 백엔드 API.
핵심 과제는 **오픈 시각에 트래픽이 몰리는 상황**에서의 동시성 제어 — 순간 대량 요청 처리, 재고 초과 예약(오버셀링) 방지.

## 기술 스택
- Kotlin, Spring Boot, Spring Data JPA
- MySQL (RDB)
- Docker, Docker Compose
- Nginx (리버스 프록시), Redis (캐시 + 분산락)
- Kafka (비동기 이벤트)
- Spring Batch, k6 (부하테스트)
- JUnit5, MockK (테스트)

## 5주 로드맵

| 주차 | 기술 | 기능 |
|---|---|---|
| 1주 | Kotlin, Spring Boot, JPA, MySQL | 공연 등록, 좌석/수량 등록, 예약 생성/조회/취소 API. 오버셀링 문제를 의도적으로 재현 |
| 2주 | Docker, Docker Compose | Spring Boot + MySQL 컨테이너화, `docker-compose up`으로 전체 기동 |
| 3주 | Nginx, Redis | 공연 상세 캐싱, Redis 분산락으로 오버셀링 해결(1주차 문제 완결), Nginx 로드밸런싱 |
| 4주 | Kafka | 예약 확정 이벤트 발행, 알림/통계 컨슈머 분리 |
| 5주 | Spring Batch, k6 | 매진 임박 통계 배치, 오픈런 부하테스트(동시 1000명), 캐시/락 적용 전후 TPS 비교 |
| 상시 병행 | GitHub Actions + AI API | PR 생성 시 AI가 코드 리뷰/N+1 등 성능 이슈 자동 코멘트하는 자동화 봇 구축 |
| 6주 (스트레치) | Kubernetes (minikube) | 기존 docker-compose 구성을 K8s manifest(Deployment/Service)로 변환해 배포. 여유 있으면 HPA로 트래픽 급증 시 자동 스케일링까지 시연 |

> S3/MinIO 대용량 파일 업로드는 기존 실무 경험이 있어 이번 프로젝트 범위에서 제외. 핵심 서사(오버셀링 → 분산락 해결 → 부하테스트 수치화)에 집중.
>
> 6주차 Kubernetes는 1~5주차 완료 후 진행하는 스트레치 목표. 로컬 RAM(8GB) 제약상 본 로드맵 진행 중 상시 실행하지 않고, 필요할 때만 minikube를 켜서 확인 후 종료.

## 왜 이 순서인가
1주차에 오버셀링 문제를 의도적으로 만들고, 3주차에 Redis 분산락으로 해결하는 구조로 설계.
"문제를 발견하고 왜 이 기술로 해결했는지"라는 서사를 통해 단순 기술 나열이 아닌 설득력 있는 스토리를 만든다.

## 코드 컨벤션 (Custom Instructions에 등록할 내용)
- 패키지는 도메인 기준(concert / tickettype / reservation / member / global)으로 분리
- Controller — Service — Repository — Domain 계층 구조 유지
- DTO는 Request/Response 분리, Entity를 API에 직접 노출하지 않음
- 재고 차감/증가 로직은 tickettype 서비스에만 위치 (3주차 분산락 적용 지점)
