
const logger = false
const fastify = require('fastify')({ logger })

const RUNTIME_PATH = '/2018-06-01/runtime'
const TIMEOUT = 5 * 1000

const port = process.env['SERVER_PORT'] ? 
    parseInt(process.env['SERVER_PORT']) : 8080

const replies = []

fastify.post('/invoke', (req, reply) => {
    const data = req.body

    console.log(`*** invoke: ${JSON.stringify(data)}`)

    replies.splice(0).forEach(r => {
        const deadline = Date.now() + TIMEOUT

        r.code(200)
            .header('Lambda-Runtime-Aws-Request-Id', data['request-id'])
            .header('Lambda-Runtime-Deadline-Ms', deadline.toString())
            .header('Lambda-Runtime-Trace-Id', data['trace-id'])
            .header('Lambda-Runtime-Invoked-Function-Arn', data['function-arn'])
            .send(data.body)
    })

    reply.code(200).send({})
})

fastify.get(`${RUNTIME_PATH}/invocation/next`, (req, reply) => {
    console.log('*** next')
    console.log(req.body)

    replies.push(reply)
})

fastify.post(`${RUNTIME_PATH}/invocation/:id/response`, (req, reply) => {
    console.log(`*** response: id = ${req.params['id']}`)
    console.log(req.body)

    reply.code(202).send({ status: '' })
})

fastify.post(`${RUNTIME_PATH}/invocation/:id/error`, (req, reply) => {
    console.log(`*** error: id = ${req.params['id']}`)
    console.log(req.body)

    reply.code(202).send({ status: '' })
})

fastify.post(`${RUNTIME_PATH}/init/error`, (req, reply) => {
    console.log('*** init error')
    console.log(req.body)

    reply.code(202).send({ status: '' })
})

fastify.listen(port)
    .then(r => console.log(`started: ${r}`))
    .catch(err => console.error(err))

