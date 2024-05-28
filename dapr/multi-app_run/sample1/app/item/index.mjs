import { DaprServer } from '@dapr/dapr'

const server = new DaprServer()

await server.invoker.listen('get', data => {
    return {
        result: {
            name: 'item-1',
            value: 123,
        }
    }
})

await server.start()
