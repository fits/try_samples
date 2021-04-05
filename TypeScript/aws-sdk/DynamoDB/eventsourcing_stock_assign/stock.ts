
import { 
    DynamoDBClient,
    PutItemCommand, UpdateItemCommand, 
    ExecuteStatementCommand, AttributeValue
} from '@aws-sdk/client-dynamodb'

import { v4 as uuidv4 } from 'uuid'
import { config } from './config'

const RETRY_NUM = 10

const SLEEP_FIRST_MAX = 500
const SLEEP_FIRST_UNIT = 5

const SLEEP_MIN = 50
const SLEEP_RANDOM = 300

const eventTable = 'StockEvents'
const revTable = 'StockEventsRevision'

const client = new DynamoDBClient(config)

type StockId = string
type Revision = string
type Quantity = number
type AssignId = string

export interface Stock {
    stockId: StockId
    revision: Revision
    qty: Quantity
    assignedQty: Quantity
}

export interface StockAssigned {
    tag: 'stock.assigned'
    stockId: StockId
    revision: Revision
    assignId: AssignId
    assignedQty: Quantity
}

export interface StockAssignCancelled {
    tag: 'stock.assign-cancelled'
    stockId: StockId
    revision: Revision
    assignId: AssignId
    cancelAssignedQty: Quantity
}

export interface StockArrived {
    tag: 'stock.arrived'
    stockId: StockId
    revision: Revision
    incoming: Quantity
}

export type StockEvent = StockAssigned | StockAssignCancelled | StockArrived

type DataItem = {[key: string]: AttributeValue}

const sleep = timeout => new Promise(resolve => setTimeout(resolve, timeout))

export class StockAction {
    static async assign(stockId: StockId, assignQty: Quantity): Promise<StockEvent> {
        const stock = await StockAction.find(stockId)

        if (!stock) {
            throw new Error('stock not found')
        }

        if (stock.qty < stock.assignedQty + assignQty) {
            throw new Error('stock out')
        }

        const nextRev = await StockAction.nextRevision(stockId)
        const assignId = `ASSIGN-${uuidv4()}`

        const event: StockAssigned = {
            tag: 'stock.assigned',
            stockId,
            revision: nextRev,
            assignId,
            assignedQty: assignQty
        }

        await StockAction.saveEvent(event)

        await StockAction.checkAssign(event, stock)

        return event
    }

    static async arrive(stockId: StockId, incoming: Quantity): Promise<StockEvent> {
        if (incoming < 0) {
            throw new Error('invalid incoming')
        }

        const event: StockEvent = {
            tag: 'stock.arrived',
            stockId,
            revision: await StockAction.nextRevision(stockId),
            incoming
        }

        await StockAction.saveEvent(event)

        return event
    }
    
    static async find(stockId: StockId): Promise<Stock | undefined> {
        const events = await StockAction.loadEvents(stockId, '0')

        if (events.length == 0) {
            return undefined
        }

        const stock: Stock = {
            stockId,
            revision: '0',
            qty: 0,
            assignedQty: 0
        }

        return events.reduce((acc, ev) => {
            switch (ev.tag) {
                case 'stock.assigned':
                    acc.revision = ev.revision
                    acc.assignedQty += ev.assignedQty
                    break
                case 'stock.assign-cancelled':
                    acc.revision = ev.revision
                    acc.assignedQty -= ev.cancelAssignedQty
                    break
                case 'stock.arrived':
                    acc.revision = ev.revision
                    acc.qty += ev.incoming
                    break
            }
            return acc
        }, stock)
    }

    private static async checkAssign(event: StockAssigned, stock: Stock) {
        const expectedSize = parseInt(event.revision) - parseInt(stock.revision) - 1

        if (expectedSize == 0) {
            return
        }

        await sleep(
            Math.min(SLEEP_FIRST_MAX, SLEEP_FIRST_UNIT * expectedSize)
        )

        for (let i = 0; i < RETRY_NUM; i++) {
            const events = await StockAction.loadEvents(
                event.stockId, 
                stock.revision, 
                event.revision
            )

            if (events.length != expectedSize) {
                console.log(`*** retry ${i + 1}: rev=${event.revision}`)

                await sleep(
                    Math.max(SLEEP_MIN, Math.floor(Math.random() * SLEEP_RANDOM))
                )

                continue
            }

            const restQty = events.reduce(
                (acc, ev) => {
                    switch (ev.tag) {
                        case 'stock.assigned':
                            if (acc >= ev.assignedQty) {
                                return acc - ev.assignedQty
                            }
                            break
                        case 'stock.arrived':
                            return acc + ev.incoming
                    }
                    return acc
                },
                Math.max(0, stock.qty - stock.assignedQty)
            )

            if (Math.max(0, restQty) < event.assignedQty) {
                const cancelRev = await StockAction.nextRevision(event.stockId)

                console.log(`*** cancel assign: rev=${event.revision}, rev_cancel=${cancelRev}, assign_qty=${event.assignedQty}`)

                await StockAction.saveEvent({
                    tag: 'stock.assign-cancelled',
                    stockId: event.stockId,
                    revision: cancelRev,
                    assignId: event.assignId,
                    cancelAssignedQty: event.assignedQty
                })

                throw new Error('stock over after')
            }

            return
        }
    }

    private static async nextRevision(stockId: StockId): Promise<Revision> {
        const res = await client.send(new UpdateItemCommand({
            TableName: revTable,
            Key: {
                StockId: { S: stockId }
            },
            AttributeUpdates: {
                Revision: { Value: { N: '1' }, Action: 'ADD' }
            },
            ReturnValues: 'UPDATED_NEW'
        }))

        return res.Attributes.Revision.N
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
            case 'stock.assign-cancelled':
                return {
                    Tag: { S: event.tag },
                    StockId: { S: event.stockId },
                    Revision: { N: event.revision },
                    AssignId: { S: event.assignId },
                    CancelAssignedQty: { N: event.cancelAssignedQty.toString() }
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
            case 'stock.assign-cancelled':
                return {
                    tag: item.Tag.S,
                    stockId: item.StockId.S,
                    revision: item.Revision.N,
                    assignId: item.AssignId.S,
                    cancelAssignedQty: parseInt(item.CancelAssignedQty.N)
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
