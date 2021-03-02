
const { DynamoDBClient, QueryCommand } = require('@aws-sdk/client-dynamodb')

const config = require('./config')

const tableName = 'events'

const id = process.argv[2]

const client = new DynamoDBClient(config)

const run = async () => {
    const r = await client.send(new QueryCommand({
        TableName: tableName,
        KeyConditionExpression: 'itemId = :id',
        ExpressionAttributeValues: {
            ':id': { S: id }
        },
        ConsistentRead: true
    }))

    console.log(r.Items)

    const item = r.Items.reduce(
        (acc, ev) => {
            if (acc.id == ev.itemId.S) {
                return {
                    id: acc.id, 
                    value: acc.value + parseInt(ev.value.N)
                }
            }
            return acc
        }, 
        { id, value: 0 }
    )

    console.log(item)
}

run().catch(err => console.error(err))
