import { createConnection } from 'mysql2/promise'

const statusList = process.argv[2].split(',').map(s => s.trim())
const date = new Date(process.argv[3])

const host = process.env.DB_HOST ?? 'localhost'
const port = parseInt(process.env.DB_PORT ?? '3306')
const user = process.env.DB_USER
const password = process.env.DB_PASS
const database = process.env.DB_NAME

const sql = `
SELECT
    id,
    name,
    status,
    created_at
FROM tasks
WHERE
    status in (?) AND
    created_at >= ? 
`

const run = async () => {
    const con = await createConnection({
        host,
        port,
        user,
        password,
        database,
        timezone: 'Z'
    })

    const [rows, _] = await con.query(sql, [statusList, date])

    console.log(rows)

    await con.end()
}

run().catch(err => console.error(err))
