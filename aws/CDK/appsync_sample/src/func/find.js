
const { find } = require('./stock')

exports.handler = async (event) => {
    console.log(`find: ${JSON.stringify(event)}`)

    return find(event.tenantId, event.item, event.location)
}
