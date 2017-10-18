
import { Cell } from 'sodiumjs'

const d1 = new Cell<number>(123)
const d2 = d1.map(v => v * 2)

d1.listen(v => console.log(`d1 = ${v}`))
d2.listen(v => console.log(`d2 = ${v}`))

const d3 = d1.lift(d2, (a, b) => a + b)

console.log(`d3 = ${d3.sample()}`)
