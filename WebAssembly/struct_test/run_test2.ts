import { create, getId, getValue, setValue } from './test2.wasm'

const d = create(123, 'abc')

console.log(d)

const printData = (x: any) => 
    console.log(`id=${getId(x)}, value=${getValue(x)}, value json=${JSON.stringify(getValue(x))}`)

printData(d)

setValue(d, 'defg')

printData(d)

setValue(d, {x: 1, y: 2})

printData(d)

setValue(d, (x: number) => x + 1)

printData(d)

const d2 = create(456, d)

console.log(`d2 id=${getId(d2)}, value.id=${getId(getValue(d2))}`)

setValue(d2, null)

printData(d2)
