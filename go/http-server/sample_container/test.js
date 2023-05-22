import http from 'k6/http'
import { check, sleep } from 'k6'

const ITER_SLEEP_TIME = 0.01

const params = {
    headers: {
        'Content-Type': 'application/json'
    }
}

export const options = {
    thresholds: {
        http_req_duration: ['p(95) < 30'],
    },
    stages: [
        { duration: '10s', target: 20 },
        { duration: '5s', target: 20 },
        { duration: '5s', target: 0 },
    ]
}

export default function() {
    const n = Math.ceil(Math.random() * 100)

    const res = http.post(
        'http://127.0.0.1:3000/create', 
        JSON.stringify({
            name: "item-" + n,
            value: n
        }),
        params
    )

    check(res, {
        'status 200': (r) => r.status == 200,
        'content': (r) => r.body.includes('id')
    })

    sleep(ITER_SLEEP_TIME)
}
