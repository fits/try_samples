
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

    input CreateInput {
        id: ID!
        value: Int!
    }

    type Query {
        find(input: FindInput!): Item
    }

    type Mutation {
        add(input: CreateInput!): Item
    }
`

const resolvers = [
    {
        kind: 'UNIT', typeName: 'Query', fieldName: 'find',
        requestMappingTemplate: `
            {
                "version": "2018-05-29",
                "operation": "GetItem",
                "key": {
                    "id": $util.dynamodb.toDynamoDBJson($ctx.args.input.id)
                },
                "consistentRead": true
            }
        `, 
        responseMappingTemplate: '$utils.toJson($context.result)', 
        dataSourceName: 'ItemTable'
    },
    {
        kind: 'UNIT', typeName: 'Mutation', fieldName: 'add',
        requestMappingTemplate: `
            {
                "version": "2018-05-29",
                "operation": "PutItem",
                "key": {
                    "id": $util.dynamodb.toDynamoDBJson($ctx.args.input.id)
                },
                "attributeValues": {
                    "value": $util.dynamodb.toDynamoDBJson($ctx.args.input.value)
                }
            }
        `, 
        responseMappingTemplate: '$utils.toJson($context.result)', 
        dataSourceName: 'ItemTable'
    },
]

const dataSources = [
    {
        type: 'AMAZON_DYNAMODB', name: 'ItemTable', 
        config: {
            endpoint: 'http://localhost:8000', 
            tableName: 'Items'
        }
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

const m = `
    mutation CreateItem($id: ID!, $value: Int!) {
        add(input: {id: $id, value: $value}) {
            id
            value
        }
    }
`

const q = `
    query FindItem($id: ID!) {
        find(input: {id: $id}) {
            id
            value
        }
    }
`

const run = async () => {
    const r1 = await graphql(simulator.schema, m, null, ctx, {id: 'id-1', value: 123})
    console.log(JSON.stringify(r1))

    const r2 = await graphql(simulator.schema, q, null, ctx, {id: 'id-1'})
    console.log(JSON.stringify(r2))
}

run().catch(err => console.error(err))
