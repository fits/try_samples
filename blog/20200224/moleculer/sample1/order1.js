
const { ServiceBroker } = require('moleculer')

const broker = new ServiceBroker()

broker.createService({
    name: 'order-service',
    actions: {
        order(ctx) {
            console.log(`# order-service.order: ${JSON.stringify(ctx.params)}`)

            ctx.emit('order.ordered', ctx.params)

            return ctx.params.id
        }
    }
})

broker.createService({
    name: 'order-check-service',
    events: {
        'order.ordered'(ctx) {
            console.log(`## order-check-service ${ctx.eventName}: ${JSON.stringify(ctx.params)}`)
        }
    }
})

const run = async () => {
    await broker.start()

    const order1 = {id: 'order1', item: 'item1', qty: 2}

    const res1 = await broker.call('order-service.order', order1)

    console.log(`order result: ${res1}`)
}

run().catch(err => console.error(err))
