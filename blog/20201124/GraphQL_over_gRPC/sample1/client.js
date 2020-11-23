
const grpc = require('@grpc/grpc-js')
const { QueryRequest } = require('./generated/proto/graphql_pb')
const { GraphQLClient } = require('./generated/proto/graphql_grpc_pb')
const { Value } = require('google-protobuf/google/protobuf/struct_pb')

const client = new GraphQLClient(
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

const query = promisify(client, 'query')

const createRequest = (q, v = null) => {
    const req = new QueryRequest()

    req.setQuery(q)
    req.setVariables(Value.fromJavaScript(v))

    return req
}

const run = async () => {
    const r1 = await query(createRequest(`
        mutation {
            create(input: { category: Extra, value: 123 }) {
                id
            }
        }
    `))

    console.log(r1.toJavaScript())

    const r2 = await query(createRequest(`
        {
            find(id: "a1") {
                id
                value
            }
        }
    `))

    console.log(r2.toJavaScript())

    const id = r1.toJavaScript().data.create.id

    const r3 = await query(createRequest(
        `
            query findItem($id: ID!) {
                find(id: $id) {
                    id
                    category
                    value
                }
            }
        `,
        { id }
    ))

    console.log(r3.toJavaScript())
}

run().catch(err => console.error(err))
