
import {graphql, buildSchema} from 'https://cdn.skypack.dev/graphql'
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
`)

const store = {}

const root = {
    create: ({ input: { category, value } }) =>{
        console.log(`*** call create: category = ${category}, value = ${value}`)

        const id = `item-${v4.generate()}`
        const item = { id, category, value }

        store[id] = item

        return item
    },
    find: ({ id }) => {
        console.log(`*** call find: ${id}`)
        return store[id]
    }
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

    const id = mr1.data.create.id

    const q1 = `
        {
            find(id: "${id}") {
                id
                category
                value
            }
        }
    `

    const qr1 = await graphql(schema, q1, root)
    console.log(qr1)

    const q2 = `
        query FindItem($p: String!) {
            find(id: $p) {
                id
                category
                value
            }
        }
    `

    const qr2 = await graphql(schema, q2, root, null, { p: mr2.data.create.id })
    console.log(qr2)

    const q3 = `
        {
            find(id: "invalid-id") {
                id
                category
                value
            }
        }
    `

    const qr3 = await graphql(schema, q3, root)
    console.log(qr3)
}

run().catch(err => console.error(err))
