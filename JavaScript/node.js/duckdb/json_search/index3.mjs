import { DuckDBInstance } from '@duckdb/node-api'

const instance = await DuckDBInstance.create()
const con = await instance.connect()

const p = await con.prepare(`
    SELECT name, value FROM data1.jsonl WHERE name LIKE $1 AND value < $2
`)

p.bindVarchar(1, 'item%')
p.bindInteger(2, 40)

const reader = await p.runAndRead()

console.log(reader.getRows()) // empty

await reader.readAll()

console.log(reader.getRows())
