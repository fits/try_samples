import { SharedArray } from 'k6/data'

const users = new SharedArray('user data', () => JSON.parse(open('./users.json')))

export const options = {
    vus: 5,
    iterations: 12,
}

export default function() {
    const user = users[(__VU - 1) % users.length]

    console.log(`__VU = ${__VU}, __ITER = ${__ITER}, id = ${user.id}, name = ${user.name}`)
}