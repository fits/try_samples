
const { 
    DynamoDBClient, 
    PutItemCommand, QueryCommand
} = require('@aws-sdk/client-dynamodb')

const config = require('./config')

const eventTable = 'StockEvents'
const snapshotTable = 'Stocks'

const client = new DynamoDBClient(config)

const toStockId = (item, location) => [item, location].join('/')
const toTenantStockId = (tenantId, stockId) => [tenantId, stockId].join('/')

const toStoreItem = (event) => {
    const stockId = toStockId(event.item, event.location)
    const tenantStockId = toTenantStockId(event.tenantId, stockId)

    const item = {
        TenantStockId: { S: tenantStockId },
        Rev: { N: event.rev },
        EventType: { S: event.type },
        TenantId: { S: event.tenantId },
        StockId: { S: stockId },
        Item: { S: event.item },
        location: { S: event.location }
    }

    if (event.hasOwnProperty('qty')) {
        item.Qty = { N: event.qty.toString() }
    }

    if (event.hasOwnProperty('assigned')) {
        item.Assigned = { N: event.assigned.toString() }
    }

    return item
}

const assign = async (tenantId, item, location, qty) => {
    if (qty <= 0) {
        throw new Error('invalid assign qty')
    }

    const stock = await find(tenantId, item, location)

    if (!stock) {
        throw new Error('not found stock')
    }

    if (stock.qty < stock.assigned + qty) {
        throw new Error('assign failed')
    }

    const event = {
        type: 'assigned',
        tenantId,
        item,
        location,
        rev: (parseInt(stock.rev) + 1).toString(),
        assigned: qty
    }

    await client.send(new PutItemCommand({
        TableName: eventTable,
        Item: toStoreItem(event),
        ConditionExpression: 
            'attribute_not_exists(TenantStockId) AND attribute_not_exists(Rev)'
    }))

    return event
}

const ship = async (tenantId, item, location, qty, assigned = 0) => {
    if (qty < 0 || assigned < 0) {
        throw new Error('invalid qty or assigned')
    }

    const stock = await find(tenantId, item, location)

    if (!stock) {
        throw new Error('not found stock')
    }

    if (assigned > stock.assigned) {
        throw new Error('invalid assigned')
    }

    if (stock.qty < (stock.assigned - assigned + qty)) {
        throw new Error('shipment failed')
    }

    const event = {
        type: 'shipped',
        tenantId,
        item,
        location,
        rev: (parseInt(stock.rev) + 1).toString(),
        qty: -qty,
        assigned: -assigned
    }

    await client.send(new PutItemCommand({
        TableName: eventTable,
        Item: toStoreItem(event),
        ConditionExpression: 
            'attribute_not_exists(TenantStockId) AND attribute_not_exists(Rev)'
    }))

    return event
}

const arrive = async (tenantId, item, location, qty) => {
    if (qty < 0) {
        throw new Error('invalid arrive qty')
    }

    const stockId = toStockId(item, location)
    const tenantStockId = toTenantStockId(tenantId, stockId)

    const es = await client.send(new QueryCommand({
        TableName: eventTable,
        KeyConditionExpression: 'TenantStockId = :tsid',
        ExpressionAttributeValues: {
            ':tsid': { S: tenantStockId }
        },
        ConsistentRead: true,
        Limit: 1,
        ScanIndexForward: false
    }))

    const rev = (es.Items.length > 0) ? parseInt(es.Items[0].Rev.N) : 0

    const event = {
        type: 'arrived',
        tenantId,
        item,
        location,
        rev: (rev + 1).toString(),
        qty
    }

    await client.send(new PutItemCommand({
        TableName: eventTable,
        Item: toStoreItem(event),
        ConditionExpression: 
            'attribute_not_exists(TenantStockId) AND attribute_not_exists(Rev)'
    }))

    return event
}

const find = async (tenantId, item, location) => {
    const stockId = toStockId(item, location)

    const snps = await client.send(new QueryCommand({
        TableName: snapshotTable,
        KeyConditionExpression: 'TenantId = :tid AND StockId = :sid',
        ExpressionAttributeValues: {
            ':tid': { S: tenantId },
            ':sid': { S: stockId }
        }
    }))

    const stock = { tenantId, stockId, item, location, rev: '0', qty: 0, assigned: 0 }

    if (snps.Items.length > 0) {
        stock.rev = snps.Items[0].Rev.N
        stock.qty = parseInt(snps.Items[0].Qty.N)
        stock.assigned = parseInt(snps.Items[0].Assigned.N)
    }

    const events = await loadEvents(tenantId, stockId, stock.rev)

    return events.reduce((acc, ev) => {
        if (ev.Qty) {
            acc.qty += parseInt(ev.Qty.N)
        }
        if (ev.Assigned) {
            acc.assigned += parseInt(ev.Assigned.N)
        }

        acc.rev = ev.Rev.N

        return acc
    }, stock)
}

const loadEvents = async (tenantId, stockId, rev) => {
    let events = []
    let nextToken = undefined

    const tenantStockId = toTenantStockId(tenantId, stockId)

    while(true) {
        const es = await client.send(new QueryCommand({
            TableName: eventTable,
            KeyConditionExpression: 'TenantStockId = :tsid AND Rev > :rev',
            ExpressionAttributeValues: {
                ':tsid': { S: tenantStockId },
                ':rev': { N: rev }
            },
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
    assign,
    ship,
    arrive,
    find
}