import { create } from 'fp-ts/lib/Date'
import { log } from 'fp-ts/lib/Console'
import { io, of, chain } from 'fp-ts/lib/IO'

const logDate = io.chain(create, log)
const logDate2 = chain(log)(create)

logDate()
logDate2()

const d = of(123)

console.log( d() )

io.chain(d, log)()
