
const { ServiceBroker } = require('moleculer')

const broker = new ServiceBroker({ logger: console })

broker.loadServices()

const run = async () => {
    await broker.start()
    await broker.call('sample1.create', { value: 123 })
}

run().catch(err => console.error(err))
