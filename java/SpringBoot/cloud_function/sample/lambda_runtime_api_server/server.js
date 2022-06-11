
const express = require('express')
const bodyParser = require('body-parser')

const RUNTIME_PATH = '/2018-06-01/runtime'
const TIMEOUT = 5 * 1000

const port = parseInt(process.env['SERVER_PORT'] ?? '8080')

const replies = []

const app = express()

app.use(bodyParser.json())
app.use(bodyParser.raw())

app.post('/invoke', (req, reply) => {
    console.log(req.body)

    const data = req.body

    replies.splice(0).forEach(r => {
        const deadline = Date.now() + TIMEOUT

        r.status(200)
            .set('Lambda-Runtime-Aws-Request-Id', data['request-id'])
            .set('Lambda-Runtime-Deadline-Ms', deadline.toString())
            .set('Lambda-Runtime-Trace-Id', data['trace-id'])
            .set('Lambda-Runtime-Invoked-Function-Arn', data['function-arn'])
            .send(data.body)
    })

    reply.status(200).send({})
})

app.get(`${RUNTIME_PATH}/invocation/next`, (req, reply) => {
    console.log('*** next')
    console.log(req.body)

    replies.push(reply)
})

app.post(`${RUNTIME_PATH}/invocation/:id/response`, (req, reply) => {
    console.log(`*** response: id = ${req.params['id']}`)
    console.log(req.body.toString('UTF-8'))

    reply.status(202).send({ status: '' })
})

app.post(`${RUNTIME_PATH}/invocation/:id/error`, (req, reply) => {
    console.log(`*** error: id = ${req.params['id']}`)
    console.log(req.body.toString('UTF-8'))

    reply.status(202).send({ status: '' })
})

app.post(`${RUNTIME_PATH}/init/error`, (req, reply) => {
    console.log('*** init error')
    console.log(req.body.toString('UTF-8'))

    reply.status(202).send({ status: '' })
})

app.listen(port, () => {
    console.log(`started: localhost:${port}`)
})
