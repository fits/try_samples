import { fastify } from 'fastify'
import { fastifyConnectPlugin } from '@connectrpc/connect-fastify'
import forge from 'node-forge'

const { pki } = forge

import type { ConnectRouter } from '@connectrpc/connect'
import { Process } from './gen/sample_connect'

const routes = (router: ConnectRouter) => router.service(Process, {
    async exec(req) {
        console.log(`called exec: ${req.command}`)
        return { result: `ok-${req.command}` }
    }
})

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

const run = async () => {
    const server = fastify({
        http2: true,
        https: {
            key: pki.privateKeyToPem(keys.privateKey),
            cert: pki.certificateToPem(cert),
        },
    })

    await server.register(fastifyConnectPlugin, { routes })

    await server.listen({ host: '0.0.0.0', port: 5001 })

    console.log(`server started: ${JSON.stringify(server.addresses())}`)
}

run().catch(err => console.error(err))
