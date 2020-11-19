
const grpc = require('@grpc/grpc-js')

const { GraphQLService } = require('./generated/proto/graphql_grpc_pb')
const { Value } = require('google-protobuf/google/protobuf/struct_pb')

const { graphql, buildSchema } = require('graphql')

const schema = buildSchema(`
    type Item {
        id: ID!
        value: Int!
    }

    type Query {
        find(id: ID!): Item
    }
`)

const root = {
    find: ({ id }) => {
        console.log(`*** call find: ${id}`)
        return { id, value: 123 }
    }
}

const server = new grpc.Server()

server.addService(GraphQLService, {
    query(call, callback) {
        try {
            const query = call.request.getQuery()
            const variables = call.request.getVariables().toJavaScript()

            graphql(schema, query, root, {}, variables)
                .then(res => 
                    callback(null, Value.fromJavaScript(res))
                )
                .catch(err => callback(err))

        } catch(e) {
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