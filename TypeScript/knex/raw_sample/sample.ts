import { knex as Knex } from 'knex'
import { pipeline } from 'stream/promises'
import { BatchTransform } from './stream_util'

const minPrice = parseInt(process.argv[2])
const batchSize = parseInt(process.env.BATCH_SIZE ?? '100')

const knex = Knex({
    client: 'mysql2',
    connection: {
        host: process.env.DB_HOST,
        port: parseInt(process.env.DB_PORT ?? '3306'),
        user: process.env.DB_USER,
        password: process.env.DB_PASS,
        database: process.env.DB_NAME
    }
})

const sql = `
    SELECT
        id, name, price 
    FROM items
    WHERE
        price >= :price
`

const run = async () => {
    await pipeline(
        knex.raw(sql, { price: minPrice }).stream(),
        new BatchTransform(batchSize),
        async (stream) => {
            for await (const rows of stream) {
                console.log(rows)
            }
        }
    )
}

run()
    .catch(err => console.error(err))
    .finally(() => knex.destroy())
