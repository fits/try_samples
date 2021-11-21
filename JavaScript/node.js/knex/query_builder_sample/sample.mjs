import knex from 'knex'

const qb = knex({ client: 'mysql' }).context.queryBuilder()

console.log(
    qb.select('*')
        .from('items')
        .join('catgories', {'categories.id': 'items.category_id'})
        .where({ 'items.price': 1000 })
        .toQuery()
)
