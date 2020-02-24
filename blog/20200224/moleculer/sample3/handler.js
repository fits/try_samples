
const { ServiceBroker } = require('moleculer')

const id = process.argv[2]

const broker = new ServiceBroker({
    nodeID: id,
    transporter: 'STAN'
})

broker.createService({
    name: 'handler',
    events: {
        'item.*'(ctx) {
            console.log(`* ${ctx.eventName}: nodeID=${broker.nodeID}, params=${JSON.stringify(ctx.params)}`)
        }
    }
})

broker.start().catch(err => console.error(err))
