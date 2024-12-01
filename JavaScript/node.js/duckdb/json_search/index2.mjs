import { DuckDBInstance } from '@duckdb/node-api'

const jsonFile = 'data/products.jsonl'

const instance = await DuckDBInstance.create()
const con = await instance.connect()

const runQuery = async (query) => {
    const reader = await con.runAndReadAll(query)

    console.log(reader.getRows())
}

await runQuery(`SELECT id, title FROM '${jsonFile}' WHERE id = 5`)

await runQuery(`SELECT id, title, meta.barcode, meta FROM '${jsonFile}' WHERE meta.barcode='6707669443381'`)

await runQuery(`
    SELECT
        id, title 
    FROM
        (SELECT id, title, unnest(reviews) as reviews FROM '${jsonFile}')
    WHERE
        reviews.rating = 1
`)

await runQuery(`
    SELECT DISTINCT
        id, title 
    FROM
        (SELECT id, title, unnest(reviews) as reviews FROM '${jsonFile}')
    WHERE
        reviews.rating = 1
`)

await runQuery(`
    WITH x AS (SELECT id, title, unnest(reviews) as reviews FROM '${jsonFile}')
    SELECT DISTINCT id, title FROM x WHERE reviews.rating = 1 ORDER BY id
`)

await runQuery(`
    SELECT
        id, title 
    FROM
        '${jsonFile}'
    WHERE
        len(list_filter(reviews, x -> x.rating = 1)) > 0
`)

await runQuery(`
    SELECT
        id, title 
    FROM
        '${jsonFile}'
    WHERE
        [x.rating FOR x IN reviews] && [1]
`)

await runQuery(`
    SELECT
        id, title 
    FROM
        '${jsonFile}'
    WHERE
        1 = ANY ([x.rating FOR x in reviews])
`)

await runQuery(`
    SELECT
        id, title 
    FROM
        '${jsonFile}'
    WHERE
        list_contains([x.rating FOR x IN reviews], 1)
`)

await runQuery(`
    SELECT
        id, title 
    FROM
        '${jsonFile}'
    WHERE
        EXISTS (FROM (SELECT unnest(reviews) AS r) WHERE r.rating = 1)
`)