
const Hapi = require('@hapi/hapi')
const { HTTP } = require('cloudevents')

const run = async () => {
    const server = Hapi.server({
        port: 3000,
        host: 'localhost'
    })

    server.route({
        method: 'POST',
        path: '/',
        handler: (req, h) => {
            const headers = req.headers
            const body = req.payload

            console.log(`headers: ${JSON.stringify(headers)}, body: ${JSON.stringify(body)}`)

            const event = HTTP.toEvent({ headers, body })

            console.log(event.toJSON())

            return ''
        }
    })

    await server.start()

    console.log(`started: ${server.info.uri}`)
}

run().catch(err => console.error(err))
