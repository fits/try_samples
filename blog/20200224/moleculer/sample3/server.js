
const { ServiceBroker } = require('moleculer')

const id = process.argv[2]

const broker = new ServiceBroker({
    nodeID: id,
    transporter: 'STAN'
})

broker.loadServices()

broker.start().catch(err => console.error(err))
