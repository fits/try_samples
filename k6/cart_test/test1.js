import { sleep, check, group } from 'k6'
import http from 'k6/http'

const BASE_URL = 'http://localhost:8080'
const ITEM_PREFIX = 'test-item-'

export const options = {
    vus: 30,
    iterations: 100,
}

const headers = { 'Content-Type': 'application/json' }

function range(n) {
    return [...Array(n).keys()]
}

function randomInt(n) {
    return Math.floor(Math.random() * n)
}

function createItemCode(n) {
    return `${ITEM_PREFIX}${n}`
}

export function setup() {
    for (const i of range(10)) {
        const itemCode = createItemCode(i + 1)

        const data = { item_code: itemCode, unit_price: randomInt(50) * 100 }

        http.post(`${BASE_URL}/items`, JSON.stringify(data), { headers })

        const r = http.get(`${BASE_URL}/items/${itemCode}`)

        const qty = 1000 - r.json().qty

        if (qty > 0) {
            http.put(`${BASE_URL}/items/${itemCode}/charge/${qty}`)
        }
    }
}

export default function() {
    const cartId = `cart-${__VU}-${__ITER}_${Date.now()}`

    // console.log(cartId)

    group('create cart', () => {
        const r = http.post(`${BASE_URL}/cart`, JSON.stringify({ cart_id: cartId }), { headers })

        check(r, {
            '200 ok': (x) => x.status === 200,
        })
    })


    group('add item', () => {
        const n = randomInt(5) + 1

        for (const _ of range(n)) {
            sleep(0.05)

            const itemCode = createItemCode(randomInt(10) + 1)
            const qty = randomInt(3) + 1

            const r2 = http.put(`${BASE_URL}/cart/${cartId}/items`, JSON.stringify({ item_code: itemCode, qty }), { headers })

            check(r2, {
                '200 ok': (x) => x.status === 200,
            })
        }
    })
}