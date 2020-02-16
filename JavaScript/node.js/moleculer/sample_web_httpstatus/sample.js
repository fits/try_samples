
const { ServiceBroker } = require('moleculer')
const { MoleculerError } = require("moleculer").Errors

const HTTPServer = require('moleculer-web')

const broker = new ServiceBroker()

broker.createService({
    name: 'web',
    mixins: [HTTPServer],
    settings: {
        routes: [
            { aliases: {
                'GET /cmd': 'sample.command'
            }}
        ]
    }
})

broker.createService({
    name: 'sample',
    actions: {
        command(ctx) {
            if (!ctx.params.id) {
                const err = new MoleculerError('invalid id parameter', 400)

                return Promise.reject(err)
                //throw err
            }

            return 'ok'
        }
    }
})

broker.start().catch(err => console.error(err))

