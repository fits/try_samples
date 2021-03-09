
const { AmplifyAppSyncSimulator } = require('amplify-appsync-simulator')
const { graphql } = require('graphql')

const schema = `
    type Item {
        id: ID!
        value: Int!
    }

    input ItemInput {
        id: ID!
    }

    type Query {
        find(input: ItemInput!): Item
    }
`

const requestMappingTemplate = `
    {
        "version": "2018-05-29",
        "operation": "Invoke",
        "payload": $utils.toJson($ctx.args.input)
    }
`

const responseMappingTemplate = '$util.toJson($ctx.result)'

const resolvers = [
    {
        kind: 'UNIT', typeName: 'Query', fieldName: 'find',
        requestMappingTemplate, responseMappingTemplate, 
        dataSourceName: 'findFunc'
    }
]

const findItem = (event) => {
    console.log(`*** invoke: event = ${JSON.stringify(event)}`)
    return { id: event.id, value: 123 }
}

const dataSources = [
    {type: 'AWS_LAMBDA', name: 'findFunc', invoke: findItem}
]

const config = {
    schema: { content: schema },
    resolvers,
    dataSources,
    appSync: {
        defaultAuthenticationType: {
            authenticationType: 'API_KEY'
        }
    }
}

const simulator = new AmplifyAppSyncSimulator()

simulator.init(config)

const ctx = {
    requestAuthorizationMode: 'API_KEY',
    appsyncErrors: []
}

const q = `
    query FindItem($id: ID!) {
        find(input: {id: $id}) {
            id
            value
        }
    }
`

const run = async () => {
    const r1 = await graphql(simulator.schema, q, null, ctx, {id: 'id-1'})
    console.log(JSON.stringify(r1))

}

run().catch(err => console.error(err))
