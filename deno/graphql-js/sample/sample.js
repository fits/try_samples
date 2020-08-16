
import {
    graphql, buildSchema, subscribe, parse
} from 'https://cdn.skypack.dev/graphql'

import { v4 } from 'https://deno.land/std/uuid/mod.ts'

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
        console.log(`*** call create: category = ${category}, value = ${value}`)

        const id = `item-${v4.generate()}`
        const item = { id, category, value }

        store[id] = item
        box.publish({ created: item })

        return item
    },
    find: ({ id }) => {
        console.log(`*** call find: ${id}`)
        return store[id]
    },
    created: () => box
}

const run = async () => {
    const m1 = `
        mutation {
            create(input: { category: Standard, value: 10 }) {
                id
            }
        }
    `

    const mr1 = await graphql(schema, m1, root)
    console.log(mr1)

    const m2 = `
        mutation Create($p: CreateItem!) {
            create(input: $p) {
                id
            }
        }
    `

    const vars = {
        p: {
            category: 'Extra',
            value: 123
        }
    }

    const mr2 = await graphql(schema, m2, root, null, vars)
    console.log(mr2)

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
        console.log(r)
    }
}

run().catch(err => console.error(err))
