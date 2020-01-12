
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
        id: 'c1',
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
        id: 'c2',
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
        id: 'c3',
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
        id: 'c4',
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

    const sp4 = await axios.post(url, {
        jsonrpc: '2.0',
        method: 'call',
        id: 'c5',
        params: {
            service: 'object',
            method: 'execute',
            args: [db, uid, password, 'stock.picking', 'button_validate', pid]
        }
    })

    console.log(sp4.data)

    if (sp4.data.error) {
        return
    }

    const vld = sp4.data.result

    const res = await axios.post(url, {
        jsonrpc: '2.0',
        method: 'call',
        id: 'c7',
        params: {
            service: 'object',
            method: 'execute',
            args: [db, uid, password, vld.res_model, 'process', vld.res_id]
        }
    })

    console.log(res.data)
}

main().catch(err => console.error(err))
