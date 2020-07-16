
const { ApolloServer, gql } = require('apollo-server')
const { v4: uuidv4 } = require('uuid')

const typeDefs = gql`
    interface Cart {
        id: ID!
    }

    type CartLine {
        item: String!
        qty: Int!
    }

    type EmptyCart implements Cart {
        id: ID!
    }

    type ActiveCart implements Cart {
        id: ID!
        lines: [CartLine]
    }

    type CancelledCart implements Cart {
        id: ID!
        cancelled: Boolean!
    }

    input ChangeQty {
        id: ID!
        item: String!
        qty: Int!
    }

    type Mutation {
        create: Cart
        changeQty(cmd: ChangeQty): Cart
        cancel(id: ID!): Cart
    }

    type Query {
        find(id: ID!): Cart
    }
`

const store = {}

const resolvers = {
    Cart: {
        __resolveType: (obj, ctx, info) => {
            if (obj.cancelled) {
                return 'CancelledCart'
            }

            if (obj.lines) {
                return 'ActiveCart'
            }

            return 'EmptyCart'
        }
    },
    Query: {
        find: (parent, { id }, ctx, info) => {
            console.log(`*** call find: ${id}`)
            return store[id]
        }
    },
    Mutation: {
        create: (parent, args, ctx, info) => {
            console.log('*** call create')

            const id = `cart-${uuidv4()}`
            const cart = { id }

            store[id] = cart

            return cart
        },
        changeQty: (parent, { cmd: { id, item, qty }}, ctx, info) =>{
            console.log(`*** call changeQty: id = ${id}, item = ${item}, qty = ${qty}`)

            let cart = store[id]

            if (cart && !cart?.cancelled) {
                const lines = cart.lines?.filter(d => d.item != item) ?? []

                if (qty > 0) {
                    lines.push({ item, qty })
                }

                if (lines?.length) {
                    cart = { id, lines }
                }
                else {
                    cart = { id }
                }

                store[id] = cart
            }

            return cart
        },
        cancel: (parent, { id }, ctx, info) => {
            console.log(`*** call cancel: id = ${id}`)

            let cart = store[id]

            if (cart) {
                cart = { id, cancelled: true }
                store[id] = cart
            }

            return cart
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
