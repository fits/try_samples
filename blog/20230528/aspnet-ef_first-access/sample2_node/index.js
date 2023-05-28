const fastify = require('fastify')()
const mariadb = require('mariadb')

const pool = mariadb.createPool({ host: 'mysql1', user: 'root', database: 'sample' })

fastify.post('/find', async (req, _rep) => {
    const input = req.body

    const conn = await pool.getConnection()

    try {
        const rows = await conn.query(
            'SELECT id, price FROM items WHERE price >= ? ORDER BY price', 
            [ input.price ]
        )

        return Object.values(rows)

    } finally {
        conn.release()
    }
})

fastify
    .listen({ host: '0.0.0.0', port: 3000 })
    .catch(err => {
        fastify.log.error(err)
        process.exit(1)
    })
