const { ServiceBroker } = require('moleculer')
const HTTPServer = require('moleculer-web')
const DbService = require('moleculer-db')
const MongoDBAdapter = require('moleculer-db-adapter-mongo')

const mongoUri = 'mongodb://localhost/sample'
const colName = 'items'

const broker = new ServiceBroker({
    nodeID: 'sample-server',
    transporter: 'STAN',
    logger: console
})

broker.createService({
    name: 'web',
    mixins: [HTTPServer],
    settings: {
        routes: [
            {aliases: {
                'GET /items/:id': 'item.get',
                'POST /items': 'item.create'
            }}
        ]
    }
})

broker.createService({
    name: 'item',
    mixins: [DbService],
    adapter: new MongoDBAdapter(mongoUri, {
         useUnifiedTopology: true
    }),
    collection: colName,
    methods: {
        async latestId() {
            const rs = await this.adapter.collection.find()
                                 .sort({ _id: -1}).limit(1).toArray()

            return (rs.length) > 0 ? rs[0]._id : 0
        }
    },
    actions: {
        async create(ctx) {
            const id = await this.latestId() + 1

            const doc = {
                _id: id,
                name: ctx.params.name
            }

            this.adapter.insert(doc)

            ctx.emit('item.created', doc)

            return id
        }
    }
})

broker.start().catch(err => console.error(err))
