
import { some, none, map } from 'https://cdn.skypack.dev/fp-ts/es6/Option.js'
import { pipe } from 'https://cdn.skypack.dev/fp-ts/es6/pipeable.js'

const f = (d: any) => 
    // @ts-ignore
    pipe(
        d,
        map( (v: number) => v + 2 ),
        map( (v: number) => v * 3 )
    )

console.log( f(some(5)) )
console.log( f(none) )
