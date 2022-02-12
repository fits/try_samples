import { createPool } from 'mysql2/promise'

const host = process.env.DB_HOST ?? 'localhost'
const port = parseInt(process.env.DB_PORT ?? '3306')
const user = process.env.DB_USER ?? 'root'
const password = process.env.DB_PASS
const database = process.env.DB_NAME

const pool = createPool({
    host,
    port,
    user,
    password,
    database,
    connectionLimit: 3
})

const range = (n: number) => [...Array(n).keys()]

const search = async (id: number) => {
    console.log(`start: ${id}`)

    const [rows] = await pool.query(
        'select * from tasks where status not in (?)',
        [ ['completed', 'failed'] ]
    )

    console.log(`result: ${id}, ${JSON.stringify(rows)}`)

    console.log(`end: ${id}`)
}

const run = async () => {
    const ps = range(10).map(search)

    console.log('wait all')

    await Promise.all(ps)

    await pool.end()
}

run().catch(err => console.error(err))
