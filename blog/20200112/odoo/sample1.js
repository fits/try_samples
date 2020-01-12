
const axios = require('axios')

const url = 'http://localhost:8069/jsonrpc'

const db = 'odoo1'
const user = 'admin@example.com'
const password = 'pass'

const productId = parseInt(process.argv[2])

const main = async () => {
    const auth = await axios.post(url, {
        jsonrpc: '2.0',
        method: 'call',
        id: 'a1',
        params: {
            service: 'common',
            method: 'authenticate',
            args: [db, user, password, {}]
        }
    })

    const uid = auth.data.result

    console.log(`uid: ${uid}`)

    const prd = await axios.post(url, {
        jsonrpc: '2.0',
        method: 'call',
        id: 'a2',
        params: {
            service: 'object',
            method: 'execute_kw',
            args: [
                db, 
                uid, 
                password, 
                'product.product', 
                'read', 
                [ productId ],
                { fields: ['name', 'product_tmpl_id', 'qty_available', 'virtual_available'] }
            ]
        }
    })

    console.log(prd.data.result)
}

main().catch(err => console.error(err))
