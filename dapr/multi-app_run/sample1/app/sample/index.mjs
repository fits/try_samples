import { DaprServer } from '@dapr/dapr'

const itemApp = process.env['APP_ITEM'] ?? 'item'

const server = new DaprServer()

await server.invoker.listen('item', async (data) => {
    const item = await server.client.invoker.invoke(itemApp, 'get')

    return {
        result: `name=${item.result.name}, value=${item.result.value}`
    }
})

await server.start()
