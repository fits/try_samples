
const axios = require('axios')

const url = 'http://localhost:8069/jsonrpc'

const db = 'odoo1'
const user = 'admin@example.com'
const password = 'pass'

const pickingTypeId = parseInt(process.argv[2])
const locationId = parseInt(process.argv[3])
const locationDestId = parseInt(process.argv[4])
const productId = parseInt(process.argv[5])
const qty = parseInt(process.argv[6])

const main = async () => {
    const auth = await axios.post(url, {
        jsonrpc: '2.0',
        method: 'call',
        id: 'd1',
        params: {
            service: 'common',
            method: 'authenticate',
            args: [db, user, password, {}]
        }
    })

    const uid = auth.data.result

    const sp1 = await axios.post(url, {
        jsonrpc: '2.0',
        method: 'call',
        id: 'd2',
        params: {
            service: 'object',
            method: 'execute',
            args: [db, uid, password, 'stock.picking', 'create', {
                picking_type_id: pickingTypeId, 
                location_id: locationId, 
                location_dest_id: locationDestId, 
                move_lines: [
                    [0, 0, {
                        name: 'move',
                        product_id: productId,
                        product_uom: 1, 
                        product_uom_qty: qty
                    }]
                ]
            }]
        }
    })

    console.log(sp1.data)

    if (sp1.data.error) {
        return
    }

    const pid = sp1.data.result

    console.log(`id: ${pid}`)

    const sp2 = await axios.post(url, {
        jsonrpc: '2.0',
        method: 'call',
        id: 'd3',
        params: {
            service: 'object',
            method: 'execute',
            args: [db, uid, password, 'stock.picking', 'action_confirm', pid]
        }
    })

    console.log(sp2.data)

    if (sp2.data.error) {
        return
    }

    const sp3 = await axios.post(url, {
        jsonrpc: '2.0',
        method: 'call',
        id: 'd4',
        params: {
            service: 'object',
            method: 'execute',
            args: [db, uid, password, 'stock.picking', 'action_assign', pid]
        }
    })

    console.log(sp3.data)

    if (sp3.data.error) {
        return
    }

    const pick = await axios.post(url, {
        jsonrpc: '2.0',
        method: 'call',
        id: 'd5',
        params: {
            service: 'object',
            method: 'execute_kw',
            args: [db, uid, password, 'stock.picking', 'read', [pid], {
                fields: ['move_line_ids']
            }]
        }
    })

    console.log(pick.data)

    const lineIds = pick.data.result[0].move_line_ids

    console.log(`move line ids: ${lineIds}`)

    const sml = await axios.post(url, {
        jsonrpc: '2.0',
        method: 'call',
        id: 'd6',
        params: {
            service: 'object',
            method: 'execute_kw',
            args: [
                db, 
                uid, 
                password, 
                'stock.move.line', 
                'write', 
                [ lineIds, { qty_done: qty }],
                {}
            ]
        }
    })

    console.log(sml.data)

    if (sml.data.error) {
        return
    }

    const sp4 = await axios.post(url, {
        jsonrpc: '2.0',
        method: 'call',
        id: 'd7',
        params: {
            service: 'object',
            method: 'execute',
            args: [db, uid, password, 'stock.picking', 'action_done', pid]
        }
    })

    console.log(sp4.data)
}

main().catch(err => console.error(err))
