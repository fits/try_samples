
const { AmplifyAppSyncSimulator } = require('amplify-appsync-simulator')
const { graphql } = require('graphql')

const schema = `
    type Item {
        id: ID!
        name: String!
    }

    type Query {
        item(id: ID!): Item
    }
`

const requestMappingTemplate = `
    {
        "id": "$context.arguments.id",
        "arguments": $util.toJson($context.arguments),
        "request": $util.toJson($context.request)
    }
`

const responseMappingTemplate = '$util.toJson($context.result)'

const resolvers = [
    {
        kind: 'UNIT', typeName: 'Query', fieldName: 'item',
        requestMappingTemplate, responseMappingTemplate, 
        dataSourceName: 'itemFindFunc'
    }
]

const dataSources = [
    {type: 'NONE', name: 'itemFindFunc'}
]

const mappingTemplates = []

const config = {
    schema: {
        content: schema
    },
    resolvers,
    mappingTemplates,
    dataSources,
    appSync: {
        name: 'test',
        defaultAuthenticationType: {
            authenticationType: 'API_KEY'
        },
        apiKey: 'fake-api-key',
        additionalAuthenticationProviders: []
    }
}

const simulator = new AmplifyAppSyncSimulator()

simulator.init(config)

simulator.getDataLoader('itemFindFunc').load = (req) => {
    const items = [
        {id: 'id-1', name: 'item-1'},
        {id: 'id-2', name: 'item-2'},
        {id: 'id-3', name: 'item-3'},
    ]

    console.log(`*** itemFindFunc.load: args = ${JSON.stringify(req)}`)

    return items.find(d => d.id == req.id)
}

const ctx = {
    requestAuthorizationMode: 'API_KEY',
    headers: {},
    appsyncErrors: []
}

const q = `
    query FindItem($id: ID!) {
        item(id: $id) {
            id
            name
        }
    }
`

const run = async () => {
    const r1 = await graphql(simulator.schema, q, null, ctx, {id: 'id-2'})
    console.log(JSON.stringify(r1))

    const r2 = await graphql(simulator.schema, q, null, ctx, {id: 'id-3'})
    console.log(JSON.stringify(r2))

    const r3 = await graphql(simulator.schema, q, null, ctx, {id: 'invalid-id'})
    console.log(JSON.stringify(r3))
}

run().catch(err => console.error(err))
