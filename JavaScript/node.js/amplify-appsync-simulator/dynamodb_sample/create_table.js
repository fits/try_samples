
const { DynamoDBClient, CreateTableCommand } = require('@aws-sdk/client-dynamodb')

const client = new DynamoDBClient({endpoint: 'http://localhost:8000'})

const run = async () => {
    const r = await client.send(new CreateTableCommand({
        TableName: 'Items',
        KeySchema: [
            { AttributeName: 'id', KeyType: 'HASH' }
        ],
        AttributeDefinitions: [
            { AttributeName: 'id', AttributeType: 'S' }
        ],
        BillingMode: 'PAY_PER_REQUEST'
    }))

    console.log(r)
}

run().catch(err => console.error(err))
