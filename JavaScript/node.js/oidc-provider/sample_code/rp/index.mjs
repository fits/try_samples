import Fastify from 'fastify'
import formbody from '@fastify/formbody'

import { Issuer, generators } from 'openid-client'

import forge from 'node-forge'
const { pki } = forge

const port = 3000

const createHttpsConfig = () => {
    const keys = pki.rsa.generateKeyPair({bits: 2048})

    const cert = pki.createCertificate()
    
    cert.publicKey = keys.publicKey
    
    cert.validity.notBefore = new Date()
    
    const expiryDate = new Date()
    expiryDate.setFullYear(expiryDate.getFullYear() + 1)
    
    cert.validity.notAfter = expiryDate
    
    const attrs = [
        { name: 'commonName', value: 'example.org' },
        { name: 'countryName', value: 'US' }
    ]
    
    cert.setSubject(attrs)
    cert.setIssuer(attrs)
    
    cert.sign(keys.privateKey)

    return {
        key: pki.privateKeyToPem(keys.privateKey),
        cert: pki.certificateToPem(cert)
    }
}

const issuer = await Issuer.discover('http://127.0.0.1:8000')

const client = new issuer.Client({
    client_id: 'sample1',
    client_secret: 'secret1',
    redirect_uris: [`https://127.0.0.1:${port}/cb`],
    response_types: ['code']
})

const code_verifier = generators.codeVerifier()

const fastify = Fastify({
    logger: true,
    https: createHttpsConfig()
})

fastify.register(formbody)

fastify.get('/auth', (_req, reply) => {
    const url = client.authorizationUrl({
        scope: 'openid',
        response_mode: 'form_post',
        response_type: 'code',
        code_challenge: generators.codeChallenge(code_verifier),
        code_challenge_method: 'S256'
    })

    console.log(url)

    reply.redirect(url)
})

fastify.post('/cb', async (req, _reply) => {
    const params = client.callbackParams(req)
    console.log(params)

    const tokens = await client.callback(undefined, params, { code_verifier })

    console.log(tokens)

    return tokens
})

try {
    await fastify.listen({ port })
} catch(err) {
    fastify.log.error(err)
    process.exit(1)
}
