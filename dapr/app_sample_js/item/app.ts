
import { DaprServer, DaprClient, DaprInvokerCallbackContent, HttpMethod } from '@dapr/dapr'

const storeName = process.env.APP_STORE ?? 'statestore'

type ItemId = string

interface ItemIdentifier {
    id: ItemId
}

interface Item extends ItemIdentifier {
    name: string
    price: number
}

type Err = { error: Error }

interface Error {
    code: string
    message: string
}

const invalidRequest: Err = { error: { code: 'ERR01', message: 'invalid request' } }
const existsItem: Err = { error: { code: 'ERR02', message: 'exists item' } }
const notFoundItem: Err = { error: { code: 'ERR03', message: 'not found item' } }

const addItem = (client: DaprClient) => async (data: DaprInvokerCallbackContent) => {
    const p = JSON.parse(data.body ?? '{}') as Item

    if (!p.id) {
        return invalidRequest
    }

    const r = await client.state.get(storeName, p.id)

    if (r) {
        return existsItem
    }

    await client.state.save(storeName, [{ key: p.id, value: p }])

    return { result: p }
}

const getItem = (client: DaprClient) => async (data: DaprInvokerCallbackContent) => {
    const p = JSON.parse(data.body ?? '{}') as ItemIdentifier

    if (!p.id) {
        return invalidRequest
    }

    const result = await client.state.get(storeName, p.id)

    if (!result) {
        return notFoundItem
    }

    return { result }
}

const run = async () => {
    const server = new DaprServer()
    const opts = { method: HttpMethod.POST }

    await server.invoker.listen('add', addItem(server.client), opts)
    await server.invoker.listen('get', getItem(server.client), opts)

    await server.start()
}

run().catch(err => {
    console.error(err)
    process.exit(1)
})