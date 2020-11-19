
const protoLoader = require('@grpc/proto-loader')
const grpc = require('@grpc/grpc-js')
const {graphql, buildSchema} = require('graphql')

const protoFile = './proto/graphql.proto'

const pd = protoLoader.loadSync(protoFile)
const proto = grpc.loadPackageDefinition(pd)

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

server.addService(proto.gql.GraphQL.service, {
    Query(call, callback) {
        const query = call.request.query
        let variables

        if (call.request.variables) {
            try {
                variables = JSON.parse(call.request.variables)
            } catch(e) {
                callback(e)
                return
            }
        }

        graphql(schema, query, root, {}, variables)
            .then(res => 
                callback(null, { result: JSON.stringify(res) })
            )
            .catch(err => callback(err))
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
