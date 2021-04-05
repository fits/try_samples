
import { 
    DynamoDBClient,
    PutItemCommand, UpdateItemCommand, 
    ExecuteStatementCommand, AttributeValue
} from '@aws-sdk/client-dynamodb'

import { v4 as uuidv4 } from 'uuid'
import { config } from './config'

const eventTable = 'StockEvents'
const stockTable = 'Stocks'

const client = new DynamoDBClient(config)

type StockId = string
type Revision = string
type Quantity = number
type AssignId = string

export interface Stock {
    stockId: StockId
    revision: Revision
    qty: Quantity
    assignedQty: Quantity,
    freeQty: Quantity
}

export interface StockAssigned {
    tag: 'stock.assigned'
    stockId: StockId
    revision: Revision
    assignId: AssignId
    assignedQty: Quantity
}

export interface StockArrived {
    tag: 'stock.arrived'
    stockId: StockId
    revision: Revision
    incoming: Quantity
}

export type StockEvent = StockAssigned | StockArrived

type DataItem = {[key: string]: AttributeValue}

export class StockAction {
    static async assign(stockId: StockId, assignQty: Quantity): Promise<StockEvent> {
        if (assignQty <= 0) {
            throw new Error('invalid assignQty')
        }

        const res = await client.send(new UpdateItemCommand({
            TableName: stockTable,
            Key: {
                StockId: { S: stockId }
            },
            UpdateExpression: 'SET #r = #r + :rev, #a = #a + :aqty, #f = #f - :aqty',
            ConditionExpression: '#f >= :aqty',
            ExpressionAttributeNames: {
                '#r': 'Revision',
                '#a': 'AssignedQty',
                '#f': 'FreeQty'
            },
            ExpressionAttributeValues: {
                ':rev': { N: '1' },
                ':aqty': { N: assignQty.toString() }
            },
            ReturnValues: 'UPDATED_NEW'
        }))

        const rev = res.Attributes.Revision.N
        const assignId = `ASSIGN-${uuidv4()}`

        const event: StockAssigned = {
            tag: 'stock.assigned',
            stockId,
            revision: rev,
            assignId,
            assignedQty: assignQty
        }

        await StockAction.saveEvent(event)

        return event
    }

    static async arrive(stockId: StockId, incoming: Quantity): Promise<StockEvent> {
        if (incoming < 0) {
            throw new Error('invalid incoming')
        }

        const stock = await StockAction.find(stockId)

        if (!stock) {
            await client.send(new PutItemCommand({
                TableName: stockTable,
                Item: {
                    StockId: { S: stockId },
                    Revision: { N: '0' },
                    Qty: { N: '0' },
                    AssignedQty: { N: '0' },
                    FreeQty: { N: '0' }
                }
            }))
        }

        const res = await client.send(new UpdateItemCommand({
            TableName: stockTable,
            Key: {
                StockId: { S: stockId }
            },
            UpdateExpression: 'SET #r = #r + :rev, #q = #q + :inc, #f = #f + :inc',
            ExpressionAttributeNames: {
                '#r': 'Revision',
                '#q': 'Qty',
                '#f': 'FreeQty'
            },
            ExpressionAttributeValues: {
                ':rev': { N: '1' },
                ':inc': { N: incoming.toString() }
            },
            ReturnValues: 'UPDATED_NEW'
        }))

        const rev = res.Attributes.Revision.N

        const event: StockEvent = {
            tag: 'stock.arrived',
            stockId,
            revision: rev,
            incoming
        }

        await StockAction.saveEvent(event)

        return event
    }

    static async find(stockId: StockId): Promise<Stock | undefined> {
        const es = await client.send(new ExecuteStatementCommand({
            Statement: `SELECT * FROM ${stockTable} WHERE StockId = ?`,
            Parameters: [
                { S: stockId }
            ],
            ConsistentRead: true
        }))

        if (es.Items.length == 0) {
            return undefined
        }

        const qty = es.Items[0].Qty ? parseInt(es.Items[0].Qty.N) : 0
        const assignedQty = es.Items[0].AssignedQty ? parseInt(es.Items[0].AssignedQty.N) : 0
        const freeQty = es.Items[0].FreeQty ? parseInt(es.Items[0].FreeQty.N) : 0

        const stock: Stock = {
            stockId,
            revision: es.Items[0].Revision.N,
            qty,
            assignedQty,
            freeQty
        }

        return stock
    }

    static async restore(stockId: StockId): Promise<Stock | undefined> {
        const events = await StockAction.loadEvents(stockId, '0')

        if (events.length == 0) {
            return undefined
        }

        const stock: Stock = {
            stockId,
            revision: '0',
            qty: 0,
            assignedQty: 0,
            freeQty: 0
        }

        return events.reduce((acc, ev) => {
            switch (ev.tag) {
                case 'stock.assigned':
                    acc.revision = ev.revision
                    acc.assignedQty += ev.assignedQty
                    acc.freeQty -= ev.assignedQty
                    break
                case 'stock.arrived':
                    acc.revision = ev.revision
                    acc.qty += ev.incoming
                    acc.freeQty += ev.incoming
                    break
            }
            return acc
        }, stock)
    }

    private static async saveEvent(event: StockEvent): Promise<void> {
        await client.send(new PutItemCommand({
            TableName: eventTable,
            Item: StockAction.toItem(event)
        }))
    }

    private static async loadEvents(stockId: StockId, 
        from: Revision, to?: Revision): Promise<Array<StockEvent>> {

        const events: Array<StockEvent> = []
        let nextToken = undefined
    
        let query = `SELECT * FROM ${eventTable} WHERE StockId = ? AND Revision > ?`
    
        const params: AttributeValue[] = [
            { S: stockId }, 
            { N: from }
        ]
    
        if (to) {
            query += ' AND Revision < ?'
            params.push({ N: to })
        }
    
        while(true) {
            const es = await client.send(new ExecuteStatementCommand({
                Statement: query,
                Parameters: params,
                ConsistentRead: true,
                NextToken: nextToken
            }))
    
            es.Items.forEach(item => {
                const ev = StockAction.toStockEvent(item)

                if (ev) {
                    events.push(ev)
                }
            })

            nextToken = es.NextToken
    
            if (!nextToken) {
                break
            }
        }

        return events
    }

    private static toItem(event: StockEvent): DataItem {
        switch (event.tag) {
            case 'stock.assigned':
                return {
                    Tag: { S: event.tag },
                    StockId: { S: event.stockId },
                    Revision: { N: event.revision },
                    AssignId: { S: event.assignId },
                    AssignedQty: { N: event.assignedQty.toString() }
                }
            case 'stock.arrived':
                return {
                    Tag: { S: event.tag },
                    StockId: { S: event.stockId },
                    Revision: { N: event.revision },
                    Incoming: { N: event.incoming.toString() }
                }
        }
    }

    private static toStockEvent(item: DataItem): StockEvent | undefined {
        switch (item.Tag.S) {
            case 'stock.assigned':
                return {
                    tag: item.Tag.S,
                    stockId: item.StockId.S,
                    revision: item.Revision.N,
                    assignId: item.AssignId.S,
                    assignedQty: parseInt(item.AssignedQty.N)
                }
            case 'stock.arrived':
                return {
                    tag: item.Tag.S,
                    stockId: item.StockId.S,
                    revision: item.Revision.N,
                    incoming: parseInt(item.Incoming.N)
                }
        }
        return undefined
    }
}
