import { create, getId, getDate, getValue, setValue } from './struct2.wasm'

const d = create(12, 'abc')

console.log(`id=${getId(d)}, date=${getDate(d).toISOString()}, value=${getValue(d)}`)

setValue(d, (x: number) => x + 1)

console.log(`id=${getId(d)}, value=${getValue(d)}, value exec=${getValue(d)(1)}`)

const d2 = create(34, d)

console.log(`id=${getId(d2)}, date=${getDate(d2).toISOString()}, value.id=${getId(getValue(d2))}, value.value=${getValue(getValue(d2))}`)

setValue(d, 'defg')

console.log(`id=${getId(d2)}, value.id=${getId(getValue(d2))}, value.value=${getValue(getValue(d2))}`)

setValue(d, new Promise(resolve => setTimeout(resolve, 1000)))

await getValue(getValue(d2))

console.log('done')
