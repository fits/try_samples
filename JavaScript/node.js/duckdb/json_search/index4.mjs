import { DuckDBInstance } from '@duckdb/node-api'

const url = 'http://localhost:8080/data1.jsonl'

const instance = await DuckDBInstance.create()
const con = await instance.connect()

const runQuery = async (query) => {
    const reader = await con.runAndReadAll(query)

    console.log(reader.getRows())
}

await runQuery(`
    SELECT * FROM '${url}'
`)

await runQuery(`
    SELECT
        name,
        d.type,
        t.detail
    FROM
        '${url}' as d
        LEFT JOIN types.csv as t ON t.type = d.type
`)
