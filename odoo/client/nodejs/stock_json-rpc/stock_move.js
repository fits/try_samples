
const odoo = require('./odoo')

const confFile = process.argv[2]
const pickingTypeId = parseInt(process.argv[3])
const locationId = parseInt(process.argv[4])
const locationDestId = parseInt(process.argv[5])
// <product_id>:<qty>,<product_id>:<qty>, ...
const moveDetails = process.argv[6]

const config = require(confFile)

const lines = moveDetails.split(',')
                            .map(d => d.split(':'))
                            .map(a => [
                                0, 0, {
                                    name: 'move',
                                    product_id: parseInt(a[0]), 
                                    product_uom: 1, 
                                    product_uom_qty: parseInt(a[1])
                                }
                            ])

const main = async () => {
    const uid = await odoo.authenticate(config)

    const ids = await odoo.execute(
        config, 
        uid, 
        'stock.picking', 
        'create', 
        [
            {
                picking_type_id: pickingTypeId, 
                location_id: locationId, 
                location_dest_id: locationDestId, 
                move_lines: lines
            }
        ]
    )

    await odoo.execute(config, uid, 'stock.picking', 'action_confirm', ids)
    await odoo.execute(config, uid, 'stock.picking', 'action_assign', ids)

    const imd = await odoo.execute(config, uid, 'stock.picking', 
                                    'button_validate', ids)

    const res = await odoo.execute(config, uid, imd.res_model, 
                                    'process', [imd.res_id])

    console.log(`result: ${res}`)
}

main().catch(err => console.error(err))
