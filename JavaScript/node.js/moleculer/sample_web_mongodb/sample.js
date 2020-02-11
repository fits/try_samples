const { ServiceBroker } = require('moleculer')
const HTTPServer = require('moleculer-web')
const DbService = require('moleculer-db')
const MongoDBAdapter = require('moleculer-db-adapter-mongo')

const mongoUri = 'mongodb://localhost/sample'
const colName = 'items'

const broker = new ServiceBroker({ logger: console })

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
    collection: colName
})

broker.start().catch(err => console.error(err))
