
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

        console.log(`*** call create: category = ${category}, value = ${value}`)

        const id = `item-${v4.generate()}`
        const item = { id, category, value }

        store[id] = item
        box.publish({ created: item })

        return item
    },
    find: (input: FindInput) => {
        const { id } = input

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

    const mr1 = await graphql(schema, m1, root, 
        undefined, undefined, undefined, undefined, undefined)

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

    const mr2 = await graphql(schema, m2, root, undefined, vars, 
        undefined, undefined, undefined)

    console.log(mr2)

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
        console.log(r)
    }
}

run().catch(err => console.error(err))
