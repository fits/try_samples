
const axios = require('axios')

const url = 'http://localhost:8069/jsonrpc'

const db = 'odoo1'
const user = 'admin@example.com'
const password = 'pass'

const productId = parseInt(process.argv[2])
const productTmplId = parseInt(process.argv[3])
const qty = parseInt(process.argv[4])

const main = async () => {
    const auth = await axios.post(url, {
        jsonrpc: '2.0',
        method: 'call',
        id: 'b1',
        params: {
            service: 'common',
            method: 'authenticate',
            args: [db, user, password, {}]
        }
    })

    const uid = auth.data.result

    const chg = await axios.post(url, {
        jsonrpc: '2.0',
        method: 'call',
        id: 'b2',
        params: {
            service: 'object',
            method: 'execute',
            args: [db, uid, password, 'stock.change.product.qty', 'create', {
                product_id: productId,
                product_tmpl_id: productTmplId,
                new_quantity: qty
            }]
        }
    })

    const sid = chg.data.result

    console.log(`create id: ${sid}`)

    const res = await axios.post(url, {
        jsonrpc: '2.0',
        method: 'call',
        id: 'b3',
        params: {
            service: 'object',
            method: 'execute',
            args: [db, uid, password, 'stock.change.product.qty', 'change_product_qty', sid]
        }
    })

    console.log(res.data.result)
}

main().catch(err => console.error(err))
