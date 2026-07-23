import http from 'k6/http';
import { Counter } from 'k6/metrics';

export const options = {
    vus: 1000,       // 가상 유저 1000명
    iterations: 1000, // 총 1000번만 실행 (1인당 딱 1번)
};

const successCount = new Counter('reservation_success');
const conflictCount = new Counter('reservation_conflict');
const otherCount = new Counter('reservation_other');

export default function () {
    const payload = JSON.stringify({
        memberId: 1,
        ticketTypeId: 7,
        quantity: 1,
    });
    const params = {
        headers: { 'Content-Type': 'application/json' },
    };

    const res = http.post('http://localhost:8080/reservations', payload, params);

    if (res.status === 201) {
        successCount.add(1);
    } else if (res.status === 409) {
        conflictCount.add(1);
    } else {
        otherCount.add(1);
    }
}