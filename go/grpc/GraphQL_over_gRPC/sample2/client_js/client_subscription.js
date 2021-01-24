const protoLoader = require('@grpc/proto-loader')
const grpc = require('@grpc/grpc-js')

const protoFile = '../proto/gql/graphql.proto'

const pd = protoLoader.loadSync(protoFile)
const proto = grpc.loadPackageDefinition(pd)

const client = new proto.gql.GraphQL(
    '127.0.0.1:50051',
    grpc.credentials.createInsecure()
)

const query = `
    subscription {
        created {
            id
            category
            value
        }
    }
`

const stream = client.Subscription({ query })

stream.on('data', res => {
    console.log(`*** received: ${JSON.stringify(res)}`)
})

stream.on('end', () => console.log('*** stream end'))
stream.on('error', err => console.log(`*** stream error: ${err}`))
