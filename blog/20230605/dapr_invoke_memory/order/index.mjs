import { DaprServer, HttpMethod } from '@dapr/dapr'
import { randomUUID } from 'crypto'

const run = async () => {
    const server = new DaprServer()
    const opts = { method: HttpMethod.GET }

    const check1 = (_) => server.client.invoker.invoke('checkout', 'checkout')
    const check2 = (_) => server.client.invoker.invoke('checkout', `checkout/${randomUUID()}`)

    await server.invoker.listen('check1', check1, opts)
    await server.invoker.listen('check2', check2, opts)

    await server.start()
}

run().catch(err => {
    console.error(err)
    process.exit(1)
})
