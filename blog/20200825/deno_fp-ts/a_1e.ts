// Runtime Error

// @deno-types="https://cdn.skypack.dev/fp-ts/es6/Option.d.ts"
import { Option, some, none, map } from 'https://cdn.skypack.dev/fp-ts/es6/Option.js'

// @deno-types="https://cdn.skypack.dev/fp-ts/es6/pipeable.d.ts"
import { pipe } from 'https://cdn.skypack.dev/fp-ts/es6/pipeable.js'

const f = (d: Option<number>) => 
    pipe(
        d,
        map( v => v + 2 ),
        map( v => v * 3 )
    )

console.log( f(some(5)) )
console.log( f(none) )
