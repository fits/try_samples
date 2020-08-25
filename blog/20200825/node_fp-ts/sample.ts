
import { Option, some, none, map } from 'fp-ts/lib/Option'
import { pipe } from 'fp-ts/lib/pipeable'

const f = (d: Option<number>) => 
    pipe(
        d,
        map(v => v + 2),
        map(v => v * 3)
    )

console.log( f(some(5)) )
console.log( f(none) )
