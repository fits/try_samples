import { DatabaseSync } from 'node:sqlite'

const items = [
    { name: 'item-1', price: 1100, category: { 'top': 'A', 'middle': 'A1' }, variants: [{'color': 'white'}, {'color': 'black'}] },
    { name: 'item-2', price: 2200, category: { 'top': 'A', 'middle': 'A1' }},
    { name: 'item-3', price: 3300, category: { 'top': 'B', 'middle': 'B3' }, variants: [{'color': 'white'}] },
    { name: 'item-4', price: 4400, category: { 'top': 'C', 'middle': 'C1' }, variants: [{'color': 'black'}, {'color': 'white'}]  },
    { name: 'item-5', price: 5500, category: { 'top': 'C', 'middle': 'C2' }, variants: [{'color': 'black'}, {'color': 'blue'}] },
]

const db = new DatabaseSync(':memory:')

db.exec(`
    CREATE TABLE item (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        data BLOB NOT NULL
    )
`)

const insert = db.prepare('INSERT INTO item (data) VALUES (jsonb(?))')

items.forEach(x => insert.run(JSON.stringify(x)))

db.prepare(`SELECT data FROM item WHERE data ->> '$.price' > 3000`)
    .all()
    .forEach(r => console.log(r.data))

console.log('-----')

db.prepare(`SELECT json(data) as data FROM item WHERE data ->> '$.price' > 3000`)
    .all()
    .forEach(r => console.log(r.data))

console.log('-----')

db.prepare(`
    SELECT
        data -> '$.name' as name, 
        data -> '$.category' as category
    FROM item
    WHERE
        data ->> '$.category.middle' = 'A1'
`).all().forEach(r => console.log(`${r.name}, ${r.category}`))

console.log('-----')

db.prepare(`
    SELECT
        data -> '$.name' as name, 
        data -> '$.variants' as variants
    FROM item
    WHERE
        data ->> '$.variants[0].color' = 'white'
`).all().forEach(r => console.log(`${r.name}, ${r.variants}`))

console.log('-----')

db.prepare(`
    SELECT
        data -> '$.name' as name, 
        data -> '$.variants' as variants
    FROM
        item,
        json_each(data, '$.variants') as V
    WHERE
        V.value ->> '$.color' = 'white'
`).all().forEach(r => console.log(`${r.name}, ${r.variants}`))
