
import { option, pipeable } from 'https://cdn.skypack.dev/fp-ts'

const { some, none, map } = option
const { pipe } = pipeable

const f = (d: any) =>
    // @ts-ignore
    pipe(
        d,
        map( (v: number) => v + 2 ),
        map( (v: number) => v * 3 )
    )

console.log( f(some(5)) )
console.log( f(none) )
