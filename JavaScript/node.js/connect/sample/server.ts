import { fastify } from 'fastify'
import { fastifyConnectPlugin } from '@connectrpc/connect-fastify'

import type { ConnectRouter } from '@connectrpc/connect'
import { Process } from './gen/sample_connect'

const routes = (router: ConnectRouter) => router.service(Process, {
    async exec(req) {
        console.log(`called exec: ${req.command}`)
        return { result: `ok-${req.command}` }
    }
})

const run = async () => {
    const server = fastify({
        http2: true,
    })

    await server.register(fastifyConnectPlugin, { routes })

    await server.listen({ host: '0.0.0.0', port: 5001 })

    console.log(`server started: ${JSON.stringify(server.addresses())}`)
}

run().catch(err => console.error(err))
