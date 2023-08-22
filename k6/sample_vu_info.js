import { sleep } from 'k6'
import exec from 'k6/execution'

export const options = {
    vus: 3,
    duration: '2s',
}

export default function() {
    console.log(`vu = ${JSON.stringify(exec.vu)}, __VU = ${__VU}, ITER = ${__ITER}`)

    sleep(1)
}