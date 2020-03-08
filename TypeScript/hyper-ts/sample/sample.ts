import * as express from 'express'
import * as H from 'hyper-ts'
import { toRequestHandler } from 'hyper-ts/lib/express'
import { pipe } from 'fp-ts/lib/pipeable'

const sample = pipe(
    H.status(H.Status.OK),
    H.ichain(() => H.closeHeaders()),
    H.ichain(() => H.send('sample1'))
)

express()
    .get('/', toRequestHandler(sample))
    .listen(3000, () => console.log('start'))
