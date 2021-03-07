
const { 
    DynamoDBClient, 
    PutItemCommand, ExecuteStatementCommand
} = require('@aws-sdk/client-dynamodb')

const config = require('./config')

const eventTable = 'ItemEvents'
const snapshotTable = 'Items'

const client = new DynamoDBClient(config)

const add = async (itemId, value) => {
    if (!itemId || value == 0) {
        throw new Error('invalid parameter')
    }

    const item = await find(itemId)

    if (item.value + value < 0) {
        throw new Error('invalid value')
    }

    const newRev = (parseInt(item.rev) + 1).toString()

    await client.send(new PutItemCommand({
        TableName: eventTable,
        Item: {
            ItemId: { S: itemId },
            Rev: { N: newRev },
            Value: { N: value.toString() }
        },
        ConditionExpression: 
            `attribute_not_exists(ItemId) AND attribute_not_exists(Rev)`
    }))

    return newRev
}

const find = async (itemId) => {
    const snps = await client.send(new ExecuteStatementCommand({
        Statement: `
            SELECT * FROM ${snapshotTable} WHERE ItemId = ?
        `,
        Parameters: [
            { S: itemId }
        ]
    }))

    const item = { itemId, rev: '0', value: 0 }

    if (snps.Items.length > 0) {
        item.rev = snps.Items[0].Rev.N
        item.value = parseInt(snps.Items[0].Value.N)
    }

    const events = await loadEvents(itemId, item.rev)

    return events.reduce((acc, ev) => {
        if (ev.Value) {
            acc.value += parseInt(ev.Value.N)
        }

        acc.rev = ev.Rev.N

        return acc
    }, item)
}

const snapshot = async (itemId) => {
    const item = await find(itemId)

    if (item) {
        await client.send(new PutItemCommand({
            TableName: snapshotTable,
            Item: {
                ItemId: { S: item.itemId },
                Rev: { N: item.rev },
                Value: { N: item.value.toString() }
            }
        }))
    }
}

const loadEvents = async (itemId, rev) => {
    let events = []
    let nextToken = undefined

    while(true) {
        const es = await client.send(new ExecuteStatementCommand({
            Statement: `
                SELECT * FROM ${eventTable} WHERE ItemId = ? AND Rev > ?
            `,
            Parameters: [
                { S: itemId },
                { N: rev }
            ],
            ConsistentRead: true,
            NextToken: nextToken
        }))

        events = events.concat(es.Items)
        nextToken = es.NextToken

        if (!nextToken) {
            break
        }
    }

    return events
}

module.exports = {
    add,
    find,
    snapshot
}