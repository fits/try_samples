import Fastify from 'fastify'
import formbody from '@fastify/formbody'

import { Issuer, generators } from 'openid-client'

import fs from 'fs'

const port = 3000

const issuer = await Issuer.discover('http://127.0.0.1:8000')

const client = new issuer.Client({
    client_id: 'sample1',
    client_secret: 'secret1',
    redirect_uris: [`https://127.0.0.1:${port}/cb`],
    response_types: ['id_token']
})

const fastify = Fastify({
    logger: true,
    https: {
        key: fs.readFileSync('./server.key'),
        cert: fs.readFileSync('./server.crt')
    }
})

fastify.register(formbody)

fastify.get('/auth', (_req, reply) => {
    const nonce = generators.nonce()

    const url = client.authorizationUrl({
        scope: 'openid',
        response_mode: 'form_post',
        nonce,
        claims: {
            id_token: { user: null, store: null }
        }
    })

    console.log(url)

    reply.redirect(url)
})

fastify.post('/cb', (req, _reply) => {
    const params = client.callbackParams(req)
    console.log(params)

    return params
})

try {
    await fastify.listen({ port })
} catch(err) {
    fastify.log.error(err)
    process.exit(1)
}
