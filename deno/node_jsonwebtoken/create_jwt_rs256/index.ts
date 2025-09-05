import { sign } from 'npm:jsonwebtoken'
import { generateKeyPairSync } from 'node:crypto'

const iss = Deno.args[1]

const { privateKey } = generateKeyPairSync(
    'rsa', 
    {
        modulusLength: 2048,
        privateKeyEncoding: {
            type: 'pkcs8',
            format: 'pem'
        }
    }
)

console.log('# private key')
console.log(privateKey)

const payload = { iss }

const token = sign(payload, privateKey, { algorithm: 'RS256' })

console.log('# JWT')
console.log(token)
