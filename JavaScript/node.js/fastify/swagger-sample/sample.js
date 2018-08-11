
const util = require('util')
const fastify = require('fastify')()

const listen = util.promisify(fastify.listen)

fastify.register(require('fastify-swagger'), {
    exposeRoute: true,
    swagger: {
        info: {
            title: 'sample'
        }
    }
})

fastify.get('/sample', {
    schema: {
        response: {
            200: {
                type: 'object',
                properties: {
                    value: {type: 'string'}
                }
            }
        }
    }
}, (req, res) => {
    res.send({value: 'sample data'})
})

listen(8080, '0.0.0.0')
    .catch(console.error)
