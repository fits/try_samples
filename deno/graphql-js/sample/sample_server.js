
import {
    graphql, buildSchema, subscribe, parse
} from 'https://cdn.skypack.dev/graphql'

import { v4 } from 'https://deno.land/std/uuid/mod.ts'
import { serve } from 'https://deno.land/std@0.64.0/http/server.ts'

const schema = buildSchema(`
    enum Category {
        Standard
        Extra
    }

    input CreateItem {
        category: Category!
        value: Int!
    }

    type Item {
        id: String!
        category: Category!
        value: Int!
    }

    type Mutation {
        create(input: CreateItem!): Item
    }

    type Query {
        find(id: String!): Item
    }

    type Subscription {
        created: Item
    }
`)

class MessageBox {
    #messages = []
    #resolves = []

    publish(value) {
        const resolve = this.#resolves.shift()

        if (resolve) {
            resolve({ value })
        }
        else {
            this.#messages.push(value)
        }
    }

    [Symbol.asyncIterator]() {
        return {
            next: () => {
                console.log('*** asyncIterator next')

                return new Promise(resolve => {
                    const value = this.#messages.shift()

                    if (value) {
                        resolve({ value })
                    }
                    else {
                        this.#resolves.push(resolve)
                    }
                })
            }
        }
    }
}

const store = {}
const box = new MessageBox()

const root = {
    create: ({ input: { category, value } }) => {
        const id = `item-${v4.generate()}`
        const item = { id, category, value }

        store[id] = item
        box.publish({ created: item })

        return item
    },
    find: ({ id }) => {
        return store[id]
    },
    created: () => box
}

const run = async () => {
    const server = serve({ port: 8080 })

    for await (const req of server) {
        const buf = await Deno.readAll(req.body)
        const query = new TextDecoder().decode(buf)

        console.log(`*** query: ${query}`)

        const res = await graphql(schema, query, root)

        console.log(res)

        req.respond({ body: JSON.stringify(res) })
    }
}

const runSubscribe = async () => {
    const s = parse(`
        subscription {
            created {
                id
                category
            }
        }
    `)

    const subsc = await subscribe(schema, s, root)

    for await (const r of subsc) {
        console.log(`*** subscribe: ${JSON.stringify(r)}`)
    }
}

Promise.all([
    run(),
    runSubscribe()
]).catch(err => console.error(err))
