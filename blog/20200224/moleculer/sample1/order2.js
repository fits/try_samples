
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
    },
    events: {
        'order.*validated'(ctx) {
            console.log(`# order-service ${ctx.eventName}: ${JSON.stringify(ctx.params)}`)
        }
    }
})

broker.createService({
    name: 'order-check-service',
    events: {
        async 'order.ordered'(ctx) {
            console.log(`## order-check-service ${ctx.eventName}: ${JSON.stringify(ctx.params)}`)

            const isValid = await ctx.call('item-service.check', ctx.params)

            if (isValid) {
                ctx.emit('order.validated', ctx.params)
            }
            else {
                ctx.emit('order.invalidated', ctx.params)
            }
        }
    }
})

broker.createService({
    name: 'item-service',
    actions: {
        check(ctx) {
            console.log(`### item-service.check: ${JSON.stringify(ctx.params)}`)
            return ['item1', 'item2'].includes(ctx.params.item)
        }
    }
})

const run = async () => {
    await broker.start()

    const order1 = {id: 'order1', item: 'item1', qty: 2}
    const order2 = {id: 'order2', item: 'item3', qty: 1}

    const res1 = await broker.call('order-service.order', order1)

    console.log(`order result: ${res1}`)

    const res2 = await broker.call('order-service.order', order2)

    console.log(`order result: ${res2}`)
}

run().catch(err => console.error(err))
