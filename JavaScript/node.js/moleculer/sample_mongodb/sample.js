const { ServiceBroker } = require('moleculer')
const DbService = require('moleculer-db')
const MongoDBAdapter = require('moleculer-db-adapter-mongo')

const mongoUri = 'mongodb://localhost/sample'
const colName = 'tasks'

const broker = new ServiceBroker({ logger: console })

broker.createService({
    name: 'task',
    mixins: [DbService],
    adapter: new MongoDBAdapter(mongoUri, {
         useUnifiedTopology: true
    }),
    collection: colName,
    actions: {
        addProcess(ctx) {
            return this.adapter.updateById(ctx.params.id, {
                '$push': {
                    details: {
                        process_id: ctx.params.processId, 
                        status: 'ready'
                    }
                }
            })
        }
    }
})

const run = async () => {
    await broker.start()

    const res1 = await broker.call('task.create', {
        name: 'task-1',
        details: [
            {process_id: 'process-1', status: 'ready'},
            {process_id: 'process-2', status: 'ready'}
        ]
    })

    console.log(res1)

    const id = res1._id

    const res2 = await broker.call('task.addProcess', {
        id: id,
        processId: 'process-3'
    })

    console.log(res2)

    const res3 = await broker.call('task.get', { id: id })

    console.log(res3)
}

run()
    .catch(err => console.error(err))
    .finally(() => broker.stop())