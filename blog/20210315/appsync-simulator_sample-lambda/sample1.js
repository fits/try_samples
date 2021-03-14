
const { AmplifyAppSyncSimulator } = require('amplify-appsync-simulator')
const { graphql } = require('graphql')

const schema = `
    type Item {
        id: ID!
        value: Int!
    }

    input FindInput {
        id: ID!
    }

    type Query {
        find(input: FindInput!): Item
    }
`

const dataSources = [
    { type: 'NONE', name: 'ItemFunc' }
]

const resolvers = [
    {
        kind: 'UNIT', 
        typeName: 'Query', fieldName: 'find',
        dataSourceName: 'ItemFunc',
        requestMappingTemplate: `
            {
                "version": "2018-05-29",
                "operation": "Invoke",
                "payload": $utils.toJson($ctx.args.input)
            }
        `,
        responseMappingTemplate: '$utils.toJson($context.result)'
    }
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

simulator.getDataLoader('ItemFunc').load = (req) => {
    console.log(`*** load: ${JSON.stringify(req)}`)

    return { id: req.payload.id, value: 123 }
}

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
    const r = await graphql(simulator.schema, q, null, ctx, {id: 'id1'})
    console.log(JSON.stringify(r))
}

run().catch(err => console.error(err))
