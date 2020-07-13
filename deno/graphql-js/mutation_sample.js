
import {graphql, buildSchema} from 'https://cdn.pika.dev/graphql'
import { v4 } from 'https://deno.land/std/uuid/mod.ts'

const schema = buildSchema(`
    input CreateItem {
        name: String!
        value: Int!
    }

    type Item {
        id: String!
        name: String!
        value: Int!
    }

    type Mutation {
        create(input: CreateItem!): Item
    }

    type Query {
        find(id: String!): Item
    }
`)

const store = {}

const root = {
    create: ({ input }) => {
        console.log(`*** call create: ${JSON.stringify(input)}`)

        const id = `item:${v4.generate()}`
        input['id'] = id

        store[id] = input

        return store[id]
    },
    find: ({ id }) => {
        console.log(`*** call find: ${id}`)
        return store[id]
    }
}

const run = async () => {
    const m = `
        mutation {
            create(input: { name: "item-1", value: 12 }) {
                id
            }
        }
    `

    const r1 = await graphql(schema, m, root)

    console.log(r1)

    const id = r1.data.create.id

    const q = `
        {
            find(id: "${id}") {
                id
                name
                value
            }
        }
    `

    const r2 = await graphql(schema, q, root)

    console.log(r2)
}

run().catch(err => console.error(err))
