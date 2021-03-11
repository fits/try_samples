
const { assign } = require('./stock')

exports.handler = async (event) => {
    console.log(`assign: ${JSON.stringify(event)}`)

    return assign(event.tenantId, event.item, event.location, event.qty)
}