
import {graphql, buildSchema} from 'https://cdn.pika.dev/graphql'

const schema = buildSchema(`
    type Item {
        id: String!
        value: Int!
    }

    type Query {
        find(id: String!): Item
    }
`)

const root = {
    find: (params) => {
        console.log(`*** call find: ${JSON.stringify(params)}`)
        return {id: params.id, value: 12}
    }
}

const run = async () => {
    const q = `
        {
            find(id: "d1") {
                id
                value
            }
        }
    `

    const r = await graphql(schema, q, root)

    console.log(r)
}

run().catch(err => console.error(err))
