
const { find } = require('./stock')

const tenantId = process.argv[2]
const item = process.argv[3]
const location = process.argv[4]

find(tenantId, item, location)
    .then(r => console.log(r))
    .catch(err => console.error(err))
