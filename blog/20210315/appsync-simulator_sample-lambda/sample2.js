
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
    { type: 'AWS_LAMBDA', name: 'ItemFunc', invoke: require('./lambda_func.js').handler }
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
    const r = await graphql(simulator.schema, q, null, ctx, {id: 'id2'})
    console.log(JSON.stringify(r))
}

run().catch(err => console.error(err))
