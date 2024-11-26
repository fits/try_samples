import { DuckDBInstance } from '@duckdb/node-api'

const instance = await DuckDBInstance.create()
const con = await instance.connect()

const reader = await con.runAndReadAll(`
    SELECT * FROM data1.jsonl WHERE value > 30
`)

const runQuery = async (query) => {
    const reader = await con.runAndReadAll(query)

    console.log(reader.getRows())
}

await runQuery(`
    SELECT * FROM data1.jsonl WHERE value > 30
`)

await runQuery(`
    SELECT name FROM data1.jsonl WHERE type = 'test1'
`)

await runQuery(`
    SELECT SUM(value) AS total FROM data1.jsonl WHERE name LIKE 'data%' AND type IS NOT NULL
`)
