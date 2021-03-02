
const { DynamoDBClient, CreateTableCommand } = require('@aws-sdk/client-dynamodb')

const config = require('./config')

const tableName = 'events'

const client = new DynamoDBClient(config)

const run = async () => {
    const cmd = new CreateTableCommand({
        TableName: tableName,
        KeySchema: [
            { AttributeName: 'itemId', KeyType: 'HASH' },
            { AttributeName: 'rev', KeyType: 'RANGE' }
        ],
        AttributeDefinitions: [
            { AttributeName: 'itemId', AttributeType: 'S' },
            { AttributeName: 'rev', AttributeType: 'N' }
        ],
        BillingMode: 'PAY_PER_REQUEST'
    })

    const res = await client.send(cmd)

    console.log(res)
}

run().catch(err => console.error(err))
