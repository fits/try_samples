
const { DynamoDBClient, CreateTableCommand } = require('@aws-sdk/client-dynamodb')

const config = require('./config')

const client = new DynamoDBClient(config)

const run = async () => {
    const r1 = await client.send(new CreateTableCommand({
        TableName: 'ItemEvents',
        KeySchema: [
            { AttributeName: 'ItemId', KeyType: 'HASH' },
            { AttributeName: 'Rev', KeyType: 'RANGE' }
        ],
        AttributeDefinitions: [
            { AttributeName: 'ItemId', AttributeType: 'S' },
            { AttributeName: 'Rev', AttributeType: 'N' }
        ],
        BillingMode: 'PAY_PER_REQUEST',
        StreamSpecification: {
            StreamEnabled: true,
            StreamViewType: 'NEW_IMAGE'
        }
    }))

    console.log(r1)

    const r2 = await client.send(new CreateTableCommand({
        TableName: 'Items',
        KeySchema: [
            { AttributeName: 'ItemId', KeyType: 'HASH' }
        ],
        AttributeDefinitions: [
            { AttributeName: 'ItemId', AttributeType: 'S' }
        ],
        BillingMode: 'PAY_PER_REQUEST'
    }))

    console.log(r2)
}

run().catch(err => console.error(err))
