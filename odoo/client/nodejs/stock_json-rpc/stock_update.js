
const odoo = require('./odoo')

const confFile = process.argv[2]
const locationId = parseInt(process.argv[3])
const productId = parseInt(process.argv[4])
const qty = parseInt(process.argv[5])

const config = require(confFile)

const main = async () => {
    const uid = await odoo.authenticate(config)

    const ids = await odoo.execute(
        config, 
        uid, 
        'stock.change.product.qty', 
        'create', 
        [
            {
                location_id: locationId, 
                product_id: productId,
                new_quantity: qty
            }
        ]
    )

    const res = await odoo.execute(
        config, 
        uid, 
        'stock.change.product.qty', 
        'change_product_qty', 
        ids
    )

    console.log(`result: ${JSON.stringify(res)}`)
}

main().catch(err => console.error(err))
