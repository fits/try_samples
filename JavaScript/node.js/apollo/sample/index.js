
const { ApolloServer, gql } = require('apollo-server')
const { v4: uuidv4 } = require('uuid')

const typeDefs = gql`
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
`

const store = {}

const resolvers = {
    Query: {
        find: (parent, { id }, ctx, info) => {
            console.log(`*** call find: ${id}`)
            return store[id]
        }
    },
    Mutation: {
        create: (parent, { input: { category, value }}, ctx, info) => {
            console.log(`*** call create: category = ${category}, value = ${value}`)

            const id = `item-${uuidv4()}`
            const item = { id, category, value }

            store[id] = item

            return item
        }
    }
}

const server = new ApolloServer({
    typeDefs,
    resolvers
})

server
    .listen()
    .then(({ url }) => console.log(`start: ${url}`))
    .catch(err => console.error(err))
