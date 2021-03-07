
const item = require('./item')

const id = process.argv[2]
const value = parseInt(process.argv[3])

item.add(id, value)
    .then(r => console.log(`rev: ${r}`))
    .catch(err => console.error(err))
