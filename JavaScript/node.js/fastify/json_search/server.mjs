import Fastify from 'fastify'
import { SampleSearcher } from './searcher.mjs'

const port = parseInt(process.env.PORT ?? '8080')
const fields = process.env.INDEX_FIELDS?.split(',').map(f => f.trim()) ?? []

const searcher = new SampleSearcher(fields)

const fastify = Fastify({
    logger: {
        level: 'warn'
    }
})

fastify.post('/update', async (req, _reply) => {
    const docs = Array.isArray(req.body) ? req.body : [req.body]

    const res = docs.map(d => searcher.store(d))

    return { id: res }
})

fastify.get('/search', async (req, _reply) => {
    const rows = Math.max(1, parseInt(req.query?.rows ?? '10'))
    const start = Math.max(0, parseInt(req.query?.start ?? '0'))
    const q = req.query?.q ?? []

    const qs = (Array.isArray(q) ? q : [q]).map(q => {
        const [f, v] = q.split(':')
        return { field: f.trim(), query: v.trim() }
    })

    const docs = searcher.search(qs)

    return {
        rows,
        start,
        total: docs.length,
        docs: docs.slice(start, start + rows)
    }
})

fastify.listen(port)
    .catch(err => {
        console.error(err)
        process.exit(1)
    })
