import http from 'k6/http'
import { check, sleep } from 'k6'

export const options = {
    vus: 20,
    iterations: 3000
}

export default function() {
    const r = http.get('http://127.0.0.1:3001/check1')
    
    check(r, { 'status 200': (r) => r.status == 200 })

    sleep(0.1)
}
