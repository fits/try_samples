
const { assign } = require('./stock')

const tenantId = process.argv[2]
const item = process.argv[3]
const location = process.argv[4]
const qty = parseInt(process.argv[5])

assign(tenantId, item, location, qty)
    .then(r => console.log(r))
    .catch(err => console.error(err))
