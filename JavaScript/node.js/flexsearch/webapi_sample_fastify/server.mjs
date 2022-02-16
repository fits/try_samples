import fastify from 'fastify'

import flexsearch from 'flexsearch'
const { Document } = flexsearch

const items = {}

const index = new Document({
    index: ['name', 'color'],
    encode: (v) => {
        return (typeof v === 'string') ? 
            [v.toLowerCase()] : [[v]]
    }
})

const app = fastify({ logger: true })

app.put('/update', async (req, reply) => {
    const item = req.body

    await index.addAsync(item)
    items[item.id] = item

    return { id: item.id }
})

app.get('/search', async (req, _reply) => {
    const q = req.query.q?.split(':') ?? ['']

    const query = (q.length > 1) ? q[1] : q[0]
    const fields = (q.length > 1) ? [q[0]] : ['name', 'color']

    console.log(`query=${query}, fields=${fields}`)

    const rs = await index.searchAsync(query, fields)
    console.log(rs)

    return rs.reduce(
        (acc, r) => acc.concat(r.result.map(id => items[id])), 
        []
    )
})

app.listen(8080)
    .catch(err => {
        app.log.error(err)
        process.exit(1)
    })
