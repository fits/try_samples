
import { Option, Some } from 'funfix-core'

const f = (o: Option<number>) => o.map(v => `value:${v}`)

const d1: Option<number> = Some(10)

console.log( d1 )
console.log( f(d1) )
console.log( f(Option.none()) )

console.log( f(d1).getOrElse('none') )
console.log( f(Option.none()).getOrElse('none') )
