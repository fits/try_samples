
const { ServiceBroker } = require('moleculer')

const broker = new ServiceBroker({ logger: console })

broker.createService({
    name: 'sample1',
    actions: {
        create(ctx) {
            console.log(`sample1.command: ${JSON.stringify(ctx.params)}`)

            ctx.emit('sample.created', {
                revision: 1,
                value: ctx.params.value
            })
        }
    }
})

broker.createService({
    name: 'sample2',
    events: {
        'sample.created'(ctx) {
            const event = ctx.params
            console.log(`sample2: ${JSON.stringify(event)}`)
        }
    }
})

broker.createService({
    name: 'sample3',
    events: {
        'sample.*'(ctx) {
            const event = ctx.params
            console.log(`sample3: ${JSON.stringify(event)}`)
        }
    }
})


const run = async () => {
    await broker.start()
    await broker.call('sample1.create', { value: 123 })
}

run().catch(err => console.error(err))
