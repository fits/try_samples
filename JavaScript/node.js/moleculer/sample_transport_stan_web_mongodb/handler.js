const { ServiceBroker } = require('moleculer')

const broker = new ServiceBroker({
    nodeID: 'sample-handler',
    transporter: 'STAN',
    logger: console
})

broker.createService({
    name: 'handler',
    events: {
        'item.created'(event) {
            console.log(event)
        }
    }
})

broker.start().catch(err => console.error(err))
