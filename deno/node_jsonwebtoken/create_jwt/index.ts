import { sign } from 'npm:jsonwebtoken'

const payload = { user: 'u1' }
const key = 'secret'

const token = sign(payload, key)

console.log(token)
