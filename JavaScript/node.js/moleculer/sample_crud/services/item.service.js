const DbService = require('moleculer-db')
const MongoDBAdapter = require('moleculer-db-adapter-mongo')

const mongoUri = process.env['MONGO_URI']
const colName = process.env['MONGO_COLLECTION_NAME']

module.exports = {
    name: 'item',
    mixins: [DbService],
    adapter: new MongoDBAdapter(mongoUri, {
         useUnifiedTopology: true
    }),
    collection: colName,
    entityCreated(entity, ctx) {
        console.log(`entityCreated: ${JSON.stringify(entity)}`)
        ctx.emit('item.created', entity)
    },
    entityUpdated(entity, ctx) {
        console.log(`entityUpdated: ${JSON.stringify(entity)}`)
        ctx.emit('item.updated', entity)
    },
    entityRemoved(entity, ctx) {
        console.log(`entityRemoved: ${JSON.stringify(entity)}`)
        ctx.emit('item.removed', entity)
    }
}