import http from 'k6/http';
import { Counter } from 'k6/metrics';

export const options = {
  vus: 1000,
  iterations: 1000,
};

const successCount = new Counter('reservation_success');
const conflictCount = new Counter('reservation_conflict');
const otherCount = new Counter('reservation_other');
const retryCount = new Counter('reservation_retry');

export default function () {
  // VU + iteration 조합으로 이 "시도" 하나당 고유한 키를 만듦 (재시도해도 이 키는 유지됨)
  const idempotencyKey = `k6-vu${__VU}-iter${__ITER}`;

  const payload = JSON.stringify({
    memberId: 1,
    ticketTypeId: 10,
    quantity: 1,
  });

  let res;
  const maxAttempts = 4; // 최초 1회 + 재시도 최대 3회

  for (let attempt = 1; attempt <= maxAttempts; attempt++) {
    const params = {
      headers: {
        'Content-Type': 'application/json',
        'Idempotency-Key': idempotencyKey,
      },
    };

    res = http.post('http://localhost:8080/reservations', payload, params);

    // 201(신규성공), 200(멱등키로 기존결과 반환), 409(재고부족) 는 명확한 응답이니 재시도 불필요
    if (res.status === 201 || res.status === 200 || res.status === 409) {
      break;
    }

    // 그 외(연결거부, 타임아웃 등 애매한 응답)는 같은 키로 재시도
    if (attempt < maxAttempts) {
      retryCount.add(1);
    }
  }

  if (res.status === 201 || res.status === 200) {
    successCount.add(1);
  } else if (res.status === 409) {
    conflictCount.add(1);
  } else {
    otherCount.add(1);
  }
}
