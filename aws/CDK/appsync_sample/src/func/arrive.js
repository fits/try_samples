
const { arrive } = require('./stock')

exports.handler = async (event) => {
    console.log(`arrive: ${JSON.stringify(event)}`)

    return arrive(event.tenantId, event.item, event.location, event.qty)
}