
const protoLoader = require('@grpc/proto-loader')
const grpc = require('@grpc/grpc-js')

const protoFile = './proto/graphql.proto'

const pd = protoLoader.loadSync(protoFile)
const proto = grpc.loadPackageDefinition(pd)

const client = new proto.gql.GraphQL(
    '127.0.0.1:50051',
    grpc.credentials.createInsecure()
)

const promisify = (obj, methodName) => args => 
    new Promise((resolve, reject) => {
        obj[methodName](args, (err, res) => {
            if (err) {
                reject(err)
            }
            else {
                resolve(res)
            }
        })
    })

const query = promisify(client, 'Query')

const run = async () => {
    const r1 = await query({query: `
        {
            find(id: "a1") {
                id
                value
            }
        }
    `})

    console.log(r1)
    console.log(JSON.parse(r1.result))

    const r2 = await query({
        query: `
            query findItem($id: ID!) {
                find(id: $id) {
                    id
                    value
                }
            }
        `,
        variables: JSON.stringify({id: 'b2'})
    })

    console.log(r2)
    console.log(JSON.parse(r2.result))
}

run().catch(err => console.error(err))
