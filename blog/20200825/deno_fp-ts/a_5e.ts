// Runtime Error

import { option, pipeable } from 'https://cdn.skypack.dev/fp-ts?dts'

const { some, none, map } = option
const { pipe } = pipeable

const f = (d: option.Option<number>) => 
    pipe(
        d,
        map( v => v + 2 ),
        map( v => v * 3 )
    )

console.log( f(some(5)) )
console.log( f(none) )
