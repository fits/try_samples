
const R = require('ramda')

const { ServiceBroker } = require('moleculer')
const { MoleculerError } = require("moleculer").Errors

const HTTPServer = require('moleculer-web')

const broker = new ServiceBroker({
    cacher: 'Memory'
})

broker.createService({
    name: 'web',
    mixins: [HTTPServer],
    settings: {
        routes: [
            { aliases: { 'REST items': 'items' } }
        ]
    }
})

const maxKey = R.pipe(R.keys, R.map(parseInt), R.reduce(R.max, 0))

const items = {}

broker.createService({
    name: 'items',
    items: {},
    methods: {
        nextId() {
            return R.toString(maxKey(items) + 1)
        }
    },
    actions: {
        list: {
            cache: true,
            rest: 'GET /',
            handler(ctx) {
                return R.values(items)
            }
        },
        get: {
            cache: {
                keys: ['id']
            },
            rest: 'GET /:id',
            handler(ctx) {
                const res = R.prop(ctx.params.id, items)

                return res ? res : Promise.reject(new MoleculerError('', 404))
            }
        },
        create: {
            rest: 'POST /',
            handler(ctx) {
                const id = this.nextId()

                console.log(items)

                items[id] = ctx.params

                this.broker.cacher.clean('items.**')

                return id
            }
        }
    }
})

broker.start().catch(err => console.error(err))

