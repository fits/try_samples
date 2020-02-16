
import * as E from 'fp-ts/lib/Either'
import { monoidProduct } from 'fp-ts/lib/Monoid'
import { pipe } from 'fp-ts/lib/pipeable'

const d1 = E.right(3)
const d2 = E.left('error')

const f = n => n * 2
const f2 = n => monoidProduct.concat(n, 3)

console.log(
	pipe(d1, E.map(f))
)

console.log(
	pipe(d1, E.map(f), E.map(f2))
)

console.log(
	pipe(d2, E.map(f))
)

console.log(
	pipe(d2, E.map(f), E.map(f2))
)
