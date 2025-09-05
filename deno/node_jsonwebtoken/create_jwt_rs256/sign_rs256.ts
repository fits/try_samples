import { sign } from 'npm:jsonwebtoken'

const key = await Deno.readTextFile(Deno.args[0]) // pem file
const iss = Deno.args[1]

const payload = { iss }

const token = sign(payload, key, { algorithm: 'RS256' })

console.log(token)
