
import { 
    DynamoDBClient, PutItemCommand, UpdateItemCommand, CreateTableCommand
} from '@aws-sdk/client-dynamodb'

const host = process.env.DYNAMO_HOST ?? 'localhost'
const port = 8000

const endpoint = `http://${host}:${port}`
const tableName = 'stocks'

const client = new DynamoDBClient({
    endpoint,
    region: 'ap-northeast-1',
    credentials: {
        accessKeyId: 'dummy',
        secretAccessKey: 'dummy'
    }
})

export const init = async (id, qty) => {
    try {
        await client.send(new CreateTableCommand({
            TableName: tableName,
            KeySchema: [
                { AttributeName: 'id', KeyType: 'HASH' }
            ],
            AttributeDefinitions: [
                { AttributeName: 'id', AttributeType: 'S' }
            ],
            BillingMode: 'PAY_PER_REQUEST'
        }))
    } catch(e) {
        if (e.$fault == 'server') {
            throw e
        }
    }

    await client.send(new PutItemCommand({
        TableName: tableName,
        Item: {
            id: {S: id},
            qty: {N: qty}
        },
    }))
}

export const assign = async (id, assignId, q) => {
    for (let i = 0; i < q; i++) {
        const k = [assignId, i + 1].join('/')

        try {
            await client.send(new UpdateItemCommand({
                TableName: tableName,
                Key: { id: {S: id} },
                UpdateExpression: 'ADD assigns :aid',
                ConditionExpression: '(attribute_not_exists(assigns) AND qty > :zero) OR qty > size(assigns)',
                ExpressionAttributeValues: {
                    ':aid': { SS: [ k ] },
                    ':zero': { N: '0' }
                }
            }))
        } catch(e) {
            if (e.$fault == 'client') {
                throw new Error(`assign failed: ${i + 1}`)
            }
            else {
                throw e
            }
        }
    }
}
