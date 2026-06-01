import { create, getId, getDate } from './testimport.wasm'

const d = create(123)

console.log(d)

console.log(`id=${getId(d)}, value=${getDate(d)}`)

console.log(getDate(d))
