
const redis = require('redis')
const { promisify } = require('util')

const client = redis.createClient()

const get = promisify(client.get).bind(client)
const set = promisify(client.set).bind(client)

set('a1', 123)
    .then(v => {
        console.log(`set result: ${v}`)
        return get('a1')
    })
    .then(v => console.log(`get result: ${v}`))
    .catch(console.error)
    .finally(() => client.quit())
