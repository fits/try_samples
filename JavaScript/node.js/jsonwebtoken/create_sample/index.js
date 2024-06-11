const jwt = require('jsonwebtoken')

const payload = {
    sub: 'test1',
}

const secret = 'test'

console.log( jwt.sign(payload, secret) )
