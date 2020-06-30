
const { ServiceBroker } = require('moleculer')
const HTTPServer = require('moleculer-web')
const { MoleculerError } = require('moleculer').Errors

const { v4: uuidv4 } = require('uuid')

const wasm = require('../../pkg/stock_management_wasm.js')
const broker = new ServiceBroker()

const store = {
    events: [],
    stocks: {}
}

const stockId = (item, location) => `${item}/${location}`

global.sample = {
    events_for_move: (id) => {
        console.log(`*** called events_for_move: ${id}`)

        const events = store.events
            .filter(e => e.move_id == id)
            .map(e => e.event)

        return { snapshot: 'Nothing', events: events }
    },
    events_for_stock: (item, location) => {
        console.log(`*** called events_for_stock: ${item}, ${location}`)

        const stock = store.stocks[stockId(item, location)]

        const events = store.events
            .filter(e => e.item == item && (e.from == location || e.to == location))
            .map(e => e.event)

        return { snapshot: stock, events: events }
    }
}

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

broker.createService({
    name: 'stock',
    actions: {
        get(ctx) {
            const res = wasm.stock_status(ctx.params.item, ctx.params.location)
            return res ? res : Promise.reject(new MoleculerError('', 404))
        },
        create(ctx) {
            const id = stockId(ctx.params.item, ctx.params.location)

            if (!(id in store.stocks)) {
                const res = wasm.new_stock(
                    ctx.params.item, 
                    ctx.params.location, 
                    ctx.params.managed
                )

                if (res) {
                    store.stocks[id] = res
                    return res
                }
            }

            return Promise.reject(new MoleculerError('', 400))
        }
    }
})

broker.createService({
    name: 'move',
    methods: {
        nextId() {
            return `move-${uuidv4()}`
        },
        save(params) {
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

            const existIndex = store.events.findIndex(e => 
                e.move_id == data.move_id && 
                e.revision == data.revision
            )

            if (existIndex < 0) {
                store.events.push(data)
                return true
            }

            return false
        },
        action(ctx, name, args) {
            const res = wasm[name](...args)

            console.log(res)

            if (res && this.save(res)) {
                ctx.emit(`stock-move.${name}`, res)
                return res.state
            }

            return Promise.reject(new MoleculerError('', 400))
        }
    },
    actions: {
        get(ctx) {
            const res = wasm.status(ctx.params.id)
            return res ? res : Promise.reject(new MoleculerError('', 404))
        },
        start(ctx) {
            const id = this.nextId()

            return this.action(ctx, 'start', [
                id, 
                ctx.params.item, 
                ctx.params.qty, 
                ctx.params.from, 
                ctx.params.to
            ])
        },
        cancel(ctx) {
            return this.action(ctx, 'cancel', [ctx.params.id])
        },
        assign(ctx) {
            return this.action(ctx, 'assign', [ctx.params.id])
        },
        shipment(ctx) {
            return this.action(ctx, 'shipment', [ctx.params.id, ctx.params.qty])
        },
        arrival(ctx) {
            return this.action(ctx, 'arrival', [ctx.params.id, ctx.params.qty])
        }
    },
    events: {
        'stock-move.*'(ctx) {
            console.log(`*** handle events: ${ctx.eventName}, ${JSON.stringify(ctx.params)}`)

        }
    }
})

broker.start().catch(err => console.error(err))
