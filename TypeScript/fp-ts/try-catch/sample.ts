import { right, orElse, tryCatch, toError } from 'fp-ts/lib/Either'
import { log } from 'fp-ts/lib/Console'

const f1 = () => 'a1'
const f2 = () => { throw 'error' }

log( tryCatch(f1, toError) )()

const r = tryCatch(f2, toError)

log(r)()

log( orElse((err: Error) => right(err.message))(r) )()
