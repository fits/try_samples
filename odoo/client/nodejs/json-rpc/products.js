
const axios = require('axios')

const url = 'http://localhost:8069/jsonrpc'

const db = process.argv[2]
const user = process.argv[3]
const pass = process.argv[4]

const call = (service, method, args) => 
    axios.post(url, {
        jsonrpc: '2.0',
        method: 'call',
        id: Date.now(),
        params: {
            service: service,
            method: method,
            args: args
        }
    })
        .then(r => new Promise((resolve, reject) => {
            if (r.data.error) {
                reject(r.data.error)
            }
            else {
                resolve(r.data.result)
            }
        }))

const authenticate = (user, pass) => 
    call('common', 'authenticate', [db, user, pass, {}])

const execute = (uid, pass, obj, method, params) =>
    call('object', 'execute', [db, uid, pass, obj, method, params])

const execute_kw = (uid, pass, obj, method, params, kw) =>
    call('object', 'execute_kw', [db, uid, pass, obj, method, params, kw])


const dump = r => {
    console.log(r)
    return r
}

authenticate(user, pass)
    .then(dump)
    .then(uid => {
        execute(uid, pass, 'product.product', 'search_count', [])
            .then(count => console.log(`count: ${count}`))

        return uid
    })
    .then(uid => {
        const kw = {fields: ['name', 'price', 'default_code']}

        execute_kw(uid, pass, 'product.product', 'search_read', [], kw)
            .then(ps => 
                ps.forEach(p => console.log(`id: ${p.id}, name: ${p.name}, price: ${p.price}, default_code: ${p.default_code}`))
            )

        return uid
    })
    .catch(err => console.error(err))
