import * as express from 'express'
import * as H from 'hyper-ts'
import { fromRequestHandler, toRequestHandler } from 'hyper-ts/lib/express'
import { pipe } from 'fp-ts/lib/pipeable'
import * as t from 'io-ts'

const jsonMiddleware = fromRequestHandler(express.json(), req => {
    console.log(req)
    return undefined
})

const parseBody = pipe(
    jsonMiddleware,
    H.ichain(() => H.decodeBody(t.object.decode))
)

const toResponse = obj => JSON.stringify({
    request_body: obj
})

const respond = obj => pipe(
    H.status(H.Status.OK),
    H.ichain(() => H.closeHeaders()),
    H.ichain(() => H.send(toResponse(obj)))
)

const sample = pipe(
    parseBody,
    H.ichain(respond)
)

express()
    .post('/', toRequestHandler(sample))
    .listen(3000, () => console.log('start'))
