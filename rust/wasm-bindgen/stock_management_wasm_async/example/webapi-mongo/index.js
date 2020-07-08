
const { ServiceBroker } = require('moleculer')
const HTTPServer = require('moleculer-web')
const { MoleculerError } = require('moleculer').Errors

const { v4: uuidv4 } = require('uuid')

const MongoClient = require('mongodb').MongoClient

const MONGO_URL = 'mongodb://localhost'
const MONGO_DB = 'stock-moves'
const MONGO_COL_STOCKS = 'stocks'
const MONGO_COL_EVENTS = 'events'

const wasm = require('../../pkg/stock_management_wasm_async.js')

const stockId = (item, location) => `${item}/${location}`

const toEvents = rs => rs.map(r => r.event).toArray()

const eventsCol = db => db.collection(MONGO_COL_EVENTS)
const stocksCol = db => db.collection(MONGO_COL_STOCKS)

const findMoveEvents = async (db, id) => {
    const rs = await eventsCol(db).find({move_id: id})
    const events = await toEvents(rs)

    console.log(events)

    return { snapshot: 'Nothing', events: events }
}

const findStockEvents = async (db, item, location) => {
    const s = await stocksCol(db).findOne({
        _id: stockId(item, location)
    })

    if (s) {
        const rs = await eventsCol(db).find({
            item: item,
            '$or': [ {from: location}, {to: location} ]
        })

        const events = await toEvents(rs)

        console.log(events)

        return { snapshot: s.stock, events: events }
    }

    return Promise.reject('not exists stock')
}

const saveEvent = async (db, params) => {
    const eventName = Object.keys(params.event)[0]

    const data = {
        move_id: params.event[eventName].move_id,
        revision: params.revision,
        type: eventName,
        item: params.event[eventName].item,
        from: params.event[eventName].from,
        to: params.event[eventName].to,
        event: params.event
    }

    const res = await eventsCol(db).updateOne(
        { move_id: data.move_id, revision: data.revision },
        { '$setOnInsert': data },
        { upsert: true }
    )

    return res.upsertedCount > 0
}

const createStock = async (db, item, location, managed) => {
    const id = stockId(item, location)

    const s = await stocksCol(db).findOne({
        _id: id
    })

    if (!s) {
        const res = await wasm.new_stock(item, location, managed)

        if (res) {
            await stocksCol(db).insertOne({
                _id: id,
                stock: res
            })

            return res
        }
    }

    return Promise.reject('failed')
}

const registGlobalFunc = db => {
    global.sample = {
        events_for_move: async (id) => {
            console.log(`*** called events_for_move: ${id}`)
            return findMoveEvents(db, id)
        },
        events_for_stock: async (item, location) => {
            console.log(`*** called events_for_stock: ${item}, ${location}`)
            return findStockEvents(db, item, location)
        }
    }
}

const createStockService = (db, broker) => {
    broker.createService({
        name: 'stock',
        actions: {
            async get(ctx) {
                const res = await wasm.stock_status(
                    ctx.params.item, 
                    ctx.params.location
                )

                return res ? res : Promise.reject(new MoleculerError('', 404))
            },
            async create(ctx) {
                return createStock(
                    db, 
                    ctx.params.item, 
                    ctx.params.location, 
                    ctx.params.managed
                )
            }
        }
    })
}

const createMoveService = (db, broker) => {
    broker.createService({
        name: 'move',
        methods: {
            nextId() {
                return `move-${uuidv4()}`
            },
            async action(ctx, name, args) {
                const res = await wasm[name](...args)
                const saved = await saveEvent(db, res)
    
                if (saved) {
                    ctx.emit(`stock-move.${name}`, res)
                    return res.state
                }
    
                return Promise.reject(new MoleculerError('', 400))
            }
        },
        actions: {
            async get(ctx) {
                const res = await wasm.status(ctx.params.id)

                return res ? res : Promise.reject(new MoleculerError('', 404))
            },
            async start(ctx) {
                const id = this.nextId()
    
                return this.action(ctx, 'start', [
                    id, 
                    ctx.params.item, 
                    ctx.params.qty, 
                    ctx.params.from, 
                    ctx.params.to
                ])
            },
            async cancel(ctx) {
                return this.action(ctx, 'cancel', [ctx.params.id])
            },
            async assign(ctx) {
                return this.action(ctx, 'assign', [ctx.params.id])
            },
            async shipment(ctx) {
                return this.action(ctx, 'shipment', [ctx.params.id, ctx.params.qty])
            },
            async arrival(ctx) {
                return this.action(ctx, 'arrival', [ctx.params.id, ctx.params.qty])
            }
        },
        events: {
            'stock-move.*'(ctx) {
                console.log(`*** handle events: ${ctx.eventName}, ${JSON.stringify(ctx.params)}`)
    
            }
        }
    })
}

const initBroker = db => {
    const broker = new ServiceBroker()

    registGlobalFunc(db)

    broker.createService({
        name: 'web',
        mixins: [HTTPServer],
        settings: {
            routes: [
                {aliases: {
                    'GET /moves/:id': 'move.get',
                    'POST /moves': 'move.start',
                    'PUT /moves/:id/cancel': 'move.cancel',
                    'PUT /moves/:id/assign': 'move.assign',
                    'PUT /moves/:id/shipment': 'move.shipment',
                    'PUT /moves/:id/arrival': 'move.arrival',
                    'GET /stocks/:item/:location': 'stock.get',
                    'PUT /stocks': 'stock.create'
                }}
            ]
        }
    })
    
    createStockService(db, broker)
    createMoveService(db, broker)

    return broker
}

const run = async () => {
    const mongo = await MongoClient.connect(
        MONGO_URL, 
        {useUnifiedTopology: true}
    )

    const db = mongo.db(MONGO_DB)
    db.createCollection(MONGO_COL_EVENTS)
    db.createCollection(MONGO_COL_STOCKS)

    initBroker(db).start()
}

run().catch(err => console.error(err))
