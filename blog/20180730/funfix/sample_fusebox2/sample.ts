
import { Option, Some } from 'funfix'

const f = (ma, mb) => ma.flatMap(a => mb.map(b => `${a} + ${b} = ${a + b}`))

const d1 = Some(10)
const d2 = Some(2)

console.log( d1 )

console.log('-----')

console.log( f(d1, d2) )
console.log( f(d1, Option.none()) )

console.log('-----')

console.log( f(d1, d2).getOrElse('none') )
console.log( f(d1, Option.none()).getOrElse('none') )
