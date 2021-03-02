
const {
    DynamoDBClient, PutItemCommand, QueryCommand
} = require('@aws-sdk/client-dynamodb')

const config = require('./config')

const tableName = 'events'

const id = process.argv[2]
const value = process.argv[3]

const client = new DynamoDBClient(config)

const run = async () => {
    const r1 = await client.send(new QueryCommand({
        TableName: tableName,
        KeyConditionExpression: 'itemId = :id',
        ExpressionAttributeValues: {
            ':id': { S: id }
        },
        ConsistentRead: true,
        Limit: 1,
        ScanIndexForward: false
    }))

    console.log(r1)

    const rev = (r1.Count > 0) ? parseInt(r1.Items[0].rev.N) : 0 

    const r2 = await client.send(new PutItemCommand({
        TableName: tableName,
        Item: {
            itemId: { S: id },
            rev: { N: (rev + 1).toString() },
            value: { N: value }
        },
        ConditionExpression: 'attribute_not_exists(itemId) AND attribute_not_exists(rev)'
    }))

    console.log(r2)
}

run().catch(err => console.error(err))