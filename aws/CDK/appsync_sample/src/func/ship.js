
const { ship } = require('./stock')

exports.handler = async (event) => {
    console.log(`ship: ${JSON.stringify(event)}`)

    return ship(event.tenantId, event.item, event.location, event.qty, event.assigned)
}