
const grpc = require('@grpc/grpc-js')
const { QueryRequest } = require('./generated/proto/graphql_pb')
const { GraphQLClient } = require('./generated/proto/graphql_grpc_pb')
const { Value } = require('google-protobuf/google/protobuf/struct_pb')

const client = new GraphQLClient(
    '127.0.0.1:50051',
    grpc.credentials.createInsecure()
)

const req = new QueryRequest()

req.setQuery(`
    subscription {
        created {
            id
            category
        }
    }
`)

req.setVariables(Value.fromJavaScript(null))

const stream = client.subscription(req)

stream.on('data', msg => {
    const event = msg.toJavaScript()
    console.log(`*** received event = ${JSON.stringify(event)}`)
})

stream.on('end', () => console.log('*** stream end'))
stream.on('error', err => console.log(`*** stream error: ${err}`))
