import { create, getId, getValue } from './struct1.wasm'

const d = create(12, 345.6)

console.log(d)
console.log(`propertyNames=${Object.getOwnPropertyNames(d)}, json=${JSON.stringify(d)}`)

console.log(`id=${getId(d)}, value=${getValue(d)}`)
