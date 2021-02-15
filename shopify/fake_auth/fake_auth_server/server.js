
const logger = true
const fastify = require('fastify')({ logger })
const crypto = require('crypto')

const proxyPort = 8580
const port = 8581

fastify.register(require('fastify-formbody'))

const settings = {
    'test1': {
        code: 'abcdef',
        secret: 'test1secret',
        shop: `127.0.0.1%3A${proxyPort}`,
        token: 'test1dummy',
    }
}

fastify.addContentTypeParser('application/json', { parseAs: 'string' }, (req, body, done) => {
    if (body === '' || body == null) {
        done(null, undefined)
        return
    }

    try {
        done(null, JSON.parse(body))
    } catch (err) {
        err.statusCode = 400
        done(err, undefined)
    }
})

fastify.get('/admin/oauth/authorize', (req, reply) => {
    console.log('*** call authorize')
    console.log(req.query)

    const {code, secret, shop} = settings[req.query['client_id']]
    const nonce = req.query['state']

    const query = `code=${code}&shop=${shop}&state=${nonce}`

    const hmac = crypto.createHmac('sha256', secret)
        .update(query)
        .digest('hex')

    const url = `${req.query['redirect_uri']}?${query}&hmac=${hmac}`

    console.log(url)

    reply.redirect(url)
})

fastify.post('/admin/oauth/access_token', (req, reply) => {
    console.log('*** call access_token')
    console.log(req.body)

    const token = settings[req.body['client_id']].token

    console.log(token)

    reply.send({
        access_token: token,
        associated_user_scope: '',
        associated_user: '',
    })
})

fastify.post('/admin/metafields.json', (req, reply) => {
    console.log('*** call metafields.json')

    const token = req.headers['x-shopify-access-token']

    console.log(`token: ${token}`)

    reply.send({})
})

fastify.listen(port)
    .then(r => console.log(r))
    .catch(err => console.error(err))
