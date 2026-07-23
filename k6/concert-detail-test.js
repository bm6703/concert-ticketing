import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    vus: 1000,        // 동시 접속자 1000명 (오픈런 시뮬레이션)
    duration: '30s',  // 30초 동안 계속 요청
};

export default function () {
    http.get('http://localhost:8080/concerts/1'); // 여기 id를 실제 값으로 수정
    sleep(1);
}