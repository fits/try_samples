
import {
    graphql, buildSchema, subscribe, parse
} from 'https://cdn.pika.dev/graphql'

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
        id: ID!
        category: Category!
        value: Int!
    }

    type Mutation {
        create(input: CreateItem!): Item
    }

    type Query {
        find(id: ID!): Item
    }

    type Subscription {
        created: Item
    }
`, {})

class MessageBox<T> {
    private messages: T[] = [] 
    private resolves: any[] = []

    publish(value: T) {
        const resolve = this.resolves.shift()

        if (resolve) {
            resolve({ value })
        }
        else {
            this.messages.push(value)
        }
    }

    [Symbol.asyncIterator]() {
        return {
            next: () => {
                console.log('*** asyncIterator next')

                return new Promise<IteratorResult<T>>(resolve => {
                    const value = this.messages.shift()

                    if (value) {
                        resolve({ value })
                    }
                    else {
                        this.resolves.push(resolve)
                    }
                })
           }
        }
    }
}

interface CreateItem {
    category: string
    value: number
}

interface CreateInput {
    input: CreateItem
}

interface Item {
    id: string
    category: string
    value: number
}

interface FindInput {
    id: string
}

type Store = { [id: string]: Item }

const store: Store = {}
const box = new MessageBox<any>()

const root = {
    create: (input: CreateInput) => {
        const { input: { category, value } } = input

        const id = `item-${v4.generate()}`
        const item = { id, category, value }

        store[id] = item
        box.publish({ created: item })

        return item
    },
    find: (input: FindInput) => {
        const { id } = input
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

        const res = await graphql(schema, query, root, 
            undefined, undefined, undefined, undefined, undefined)

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
    `, {})

    const subsc = await subscribe(schema, s, root, 
        undefined, undefined, undefined, undefined, undefined)

    for await (const r of subsc) {
        console.log(`*** subscribe: ${JSON.stringify(r)}`)
    }
}

Promise.all([
    run(),
    runSubscribe()
]).catch(err => console.error(err))
