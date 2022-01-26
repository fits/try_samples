import { knex } from 'knex'

const q = `
SELECT
    id,
    name,
    status
FROM tasks
WHERE
    name like :name AND
    created_at <= :date AND
    status in (:status) AND
    expired > :date
`

const params = {
    name: 'test%',
    status: ['created', 'confirmed'],
    date: new Date()
}

const { sql, bindings } = 
    knex({client: 'mysql2'})
        .raw(q, params)
        .toSQL()
        .toNative()

console.log(sql)
console.log(bindings)
