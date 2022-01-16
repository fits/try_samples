import { knex as Knex } from 'knex'
import KnexMysql2 from 'knex/lib/dialects/mysql2'
import { BatchTransform } from './stream_util'

const minPrice = parseInt(process.argv[2])
const batchSize = parseInt(process.env.BATCH_SIZE ?? '100')

const knex = Knex({
    client: KnexMysql2,
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

const stream = knex.raw(sql, { price: minPrice }).stream()

stream.on('error', e => {
    console.error(e)
    knex.destroy()
})

stream.on('finish', () => {
    console.log('*** finish')
    knex.destroy()
})

const batchStream = stream.pipe(new BatchTransform(batchSize))

batchStream.on('data', rows => {
    console.log(rows)
})
