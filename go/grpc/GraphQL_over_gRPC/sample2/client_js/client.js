const protoLoader = require('@grpc/proto-loader')
const grpc = require('@grpc/grpc-js')

const protoFile = '../proto/gql/graphql.proto'

const category = process.argv[2]
const value = parseInt(process.argv[3])

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
    const q1 = `
        mutation createItem($category: Category!, $value: Int!) {
            create(input: { category: $category, value: $value }) {
                id
            }
        }
    `

    const v1 = {
        structValue: {
            fields: {
                "category": {stringValue: category}, 
                "value": {numberValue: value}
            }
        }
    }

    const r1 = await query({ query: q1, variables: v1 })

    const id = r1.fields.data.structValue.fields.create.structValue.fields.id.stringValue

    console.log(id)

    const q2 = `
        query findItem($id: ID!) {
            find(id: $id) {
                id
                category
                value
            }
        }
    `
    const v2 = {
        structValue: {
            fields: {
                "id": {stringValue: id}
            }
        }
    }

    const r2 = await query({ query: q2, variables: v2 })

    console.log(JSON.stringify(r2))
}

run().catch(err => console.error(err))
