
import { promisify } from 'util'
import * as redis from 'redis'

const host = process.env.REDIS_HOST ?? 'localhost'
const port = 6379

const client = redis.createClient(port, host)

const stockKey = (id) => `stock.${id}`
const assignKey = (id) => `assign.${id}`

const getAsync = promisify(client.get).bind(client)
const setAsync = promisify(client.set).bind(client)
const delAsync = promisify(client.del).bind(client)
const llenAsync = promisify(client.llen).bind(client)
const watchAsync = promisify(client.watch).bind(client)

const multiExecAsync = f => new Promise((resolve, reject) => {
    f(client.multi())
        .exec((err, results) => {
            if (err) {
                reject(err)
            }
            else {
                resolve(results)
            }
        })
})

export const init = async (id, qty) => {
    await setAsync(stockKey(id), qty)
    await delAsync(assignKey(id))
}

export const assign = async (id, assignId, q) => {
    const stock = await getAsync(stockKey(id))

    const ak = assignKey(id)
    const w = await watchAsync(ak)

    if (w !== 'OK') {
        throw new Error('failed watch')
    }

    const assignedQty = await llenAsync(ak)

    if (stock > 0 && stock - assignedQty >= q) {
        const ds = Array(q).fill(assignId)
        await multiExecAsync(m => m.lpush(ak, ds))
    }
    else {
        throw new Error('assign failed')
    }
}
