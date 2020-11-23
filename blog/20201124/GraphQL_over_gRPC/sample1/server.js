
const grpc = require('@grpc/grpc-js')
const { GraphQLService } = require('./generated/proto/graphql_grpc_pb')
const { Struct } = require('google-protobuf/google/protobuf/struct_pb')

const { graphql, buildSchema } = require('graphql')

const { v4: uuidv4 } = require('uuid')

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
`)

const store = {}

const root = {
    create: ({ input: { category, value } }) => {
        console.log(`*** call create: category = ${category}, value = ${value}`)

        const id = `item-${uuidv4()}`
        const item = { id, category, value }

        store[id] = item

        return item
    },
    find: ({ id }) => {
        console.log(`*** call find: ${id}`)
        return store[id]
    }
}

const server = new grpc.Server()

server.addService(GraphQLService, {
    async query(call, callback) {
        try {
            const query = call.request.getQuery()
            const variables = call.request.getVariables().toJavaScript()

            const r = await graphql(schema, query, root, {}, variables)

            callback(null, Struct.fromJavaScript(r))

        } catch(e) {
            console.error(e)
            callback(e)
        }
    }
})

server.bindAsync(
    '127.0.0.1:50051',
    grpc.ServerCredentials.createInsecure(),
    (err, port) => {
        if (err) {
            console.error(err)
            return
        }

        console.log(`start server: ${port}`)

        server.start()
    }
)