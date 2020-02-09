
const { ServiceBroker } = require('moleculer')

const broker = new ServiceBroker({ logger: console })

broker.createService({
    name: 'sample1',
    actions: {
        command(ctx) {
            console.log(`sample1.command: ${JSON.stringify(ctx.params)}`)

            return ctx.call('sample2.command', {
                revision: ctx.params.revision + 1,
                message: ctx.params.message
            })
        }
    }
})

broker.createService({
    name: 'sample2',
    actions: {
        command(ctx) {
            console.log(`sample2.command: ${JSON.stringify(ctx.params)}`)
            return 'ok'
        }
    }
})

const run = async () => {
    await broker.start()

    const res = await broker.call('sample1.command', {
        revision: 1,
        message: 'test'
    })

    console.log(res)
}

run().catch(err => console.error(err))
