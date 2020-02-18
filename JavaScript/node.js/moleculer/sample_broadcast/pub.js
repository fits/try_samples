
const { ServiceBroker } = require('moleculer')

const HTTPServer = require('moleculer-web')

const broker = new ServiceBroker({
    nodeID: 'pub',
    transporter: 'STAN'
})

broker.createService({
    name: 'web',
    mixins: [HTTPServer],
    settings: {
        routes: [
            { aliases: {
                'PUT /pub': 'pub.publish'
            }}
        ]
    }
})

broker.createService({
    name: 'pub',
    actions: {
        async publish(ctx) {
            const params = ctx.params

            const res = await ctx.call('sub.direct', params)

            ctx.emit('event.emit', params)
            ctx.broadcast('event.broadcast', params)

            return res
        }
    }
})

broker.start().catch(err => console.error(err))
