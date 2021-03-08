
const { ship } = require('./stock')

const tenantId = process.argv[2]
const item = process.argv[3]
const location = process.argv[4]
const qty = parseInt(process.argv[5])
const assigned = (process.argv.length > 6) ? parseInt(process.argv[6]) : 0

ship(tenantId, item, location, qty, assigned)
    .then(r => console.log(r))
    .catch(err => console.error(err))
