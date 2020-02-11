
const { ServiceBroker } = require('moleculer')

const broker = new ServiceBroker({
    nodeID: 'sample2',
    transporter: 'STAN',
    logger: console
})

broker.loadService('./sample2.service')

broker.start().catch(err => console.error(err))
