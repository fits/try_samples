
const util = require('util')
const fastify = require('fastify')({logger: true})

const listen = util.promisify(fastify.listen)

const items = [
    {id: 'a1', name: 'item1'},
    {id: 'a2', name: 'item2'}
]

fastify.get('/items', (req, res) => {
    res.header('Access-Control-Allow-Origin', '*')
    res.send(items)
})

listen(3000, '0.0.0.0')
    .catch(console.error)
