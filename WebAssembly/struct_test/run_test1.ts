import { create, getId, getValue } from './test1.wasm'

const d = create(12, 345.6)

console.log(d)
console.log(JSON.stringify(d))

console.log(`id=${getId(d)}, value=${getValue(d)}`)
