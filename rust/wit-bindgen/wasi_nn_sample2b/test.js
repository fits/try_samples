import http from 'k6/http'
import { check } from 'k6'

const ENDPOINT = 'http://127.0.0.1:8080/'

export const options = {
    stages: [
        { duration: '1s', target: 50 },
        { duration: '1s', target: 0 },
    ]
}

export default function() {
    const checker = {
        'status is 200': r => r.status === 200,
    }

    const a = Math.random() * 10
    const b = Math.random() * 10

    const res = http.get(`${ENDPOINT}?a=${a}&b=${b}`)

    check(res, checker)
}
