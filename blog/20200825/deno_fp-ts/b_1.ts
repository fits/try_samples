
import { createRequire } from 'https://deno.land/std/node/module.ts'

const require = createRequire(import.meta.url)

const { some, none, map } = require('fp-ts/lib/Option')
const { pipe } = require('fp-ts/lib/pipeable')

const f = (d: any) => 
    pipe(
        d,
        map( (v: number) => v + 2),
        map( (v: number) => v * 3)
    )

console.log( f(some(5)) )
console.log( f(none) )
