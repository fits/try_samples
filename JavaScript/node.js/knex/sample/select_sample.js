
const config = require('./setting.json')

const knex = require('knex')({
    client: 'pg',
    connection: config
})

const main = async () => {
    const rows = await knex.select().from('stock_move').where('state', 'done')

    rows.forEach(r => 
        console.log(`id: ${r.id}, name: ${r.name}, qty: ${r.product_qty}, state: ${r.state}`)
    )

    console.log('-----')

    const rows2 = await knex({
                            sm: 'stock_move', 
                            pp: 'product_product', 
                            pt: 'product_template'
                        })
                        .select([
                            'sm.date',
                            'pt.name',
                            'sm.product_qty',
                            'sm.state'
                        ])
                        .whereRaw('sm.product_id = pp.id')
                        .andWhereRaw('pp.product_tmpl_id = pt.id')
                        .andWhere('sm.state', 'done')

    rows2.forEach(r => 
        console.log(`product: ${r.name}, qty: ${r.product_qty}, state: ${r.state}, date: ${r.date.toISOString()}`)
    )

    knex.destroy()
}

main().catch(err => {
    console.error(err)
    knex.destroy()
})
