
const axios = require('axios')

const url = 'http://localhost:3000/items'

axios.get(url)
    .then(res => console.log(res.data))
    .catch(err => console.error(err))
