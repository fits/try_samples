
import { DynamoDBClient, CreateTableCommand } from '@aws-sdk/client-dynamodb'
import { config } from './config'

const client = new DynamoDBClient(config)

const run = async () => {
    const r1 = await client.send(new CreateTableCommand({
        TableName: 'StockEvents',
        KeySchema: [
            { AttributeName: 'StockId', KeyType: 'HASH' },
            { AttributeName: 'Revision', KeyType: 'RANGE' },
        ],
        AttributeDefinitions: [
            { AttributeName: 'StockId', AttributeType: 'S' },
            { AttributeName: 'Revision', AttributeType: 'N' },
        ],
        BillingMode: 'PAY_PER_REQUEST'
    }))

    console.log(r1)

    const r2 = await client.send(new CreateTableCommand({
        TableName: 'Stocks',
        KeySchema: [
            { AttributeName: 'StockId', KeyType: 'HASH' }
        ],
        AttributeDefinitions: [
            { AttributeName: 'StockId', AttributeType: 'S' }
        ],
        BillingMode: 'PAY_PER_REQUEST'
    }))

    console.log(r2)
}

run().catch(err => console.error(err))
