const { ServiceBroker } = require('moleculer')

const broker = new ServiceBroker({
    nodeID: 'sample-handler',
    transporter: 'STAN',
    logger: console
})

broker.createService({
    name: 'handler',
    events: {
        'item.created'(ctx) {
            const event = ctx.params
            console.log(event)
        }
    }
})

broker.start().catch(err => console.error(err))
