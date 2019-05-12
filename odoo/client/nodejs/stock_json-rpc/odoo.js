
const axios = require('axios')
const uuidv4 = require('uuid/v4')

const call = (url, service, method, args) => 
    axios.post(
        url, 
        {
            jsonrpc: '2.0',
            method: 'call',
            id: uuidv4(),
            params: {
                service: service,
                method: method,
                args: args
            }
        }
    ).then(r =>
        new Promise((resolve, reject) => {
            if (r.data.error) {
                reject(r.data.error)
            }
            else {
                resolve(r.data.result)
            }
        })
    )

exports.authenticate = config => 
    call(config.url, 'common', 'authenticate', [config.db, config.user, config.pass, {}])

exports.execute = (config, uid, obj, method, params) =>
    call(config.url, 'object', 'execute', [config.db, uid, config.pass, obj, method, params])

exports.execute_kw = (config, uid, obj, method, params, kw = {}) =>
    call(config.url, 'object', 'execute_kw', [config.db, uid, config.pass, obj, method, params, kw])
