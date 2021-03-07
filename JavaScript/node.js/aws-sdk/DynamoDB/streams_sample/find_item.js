const item = require('./item')

const id = process.argv[2]

item.find(id)
    .then(r => console.log(r))
    .catch(err => console.error(err))
