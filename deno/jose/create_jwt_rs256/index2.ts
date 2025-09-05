import { exportJWK, generateKeyPair, SignJWT } from 'npm:jose'

const alg = 'RS256'

const issuer = 'http://test.example.com'
const user = Deno.args[0]

const { publicKey, privateKey } = await generateKeyPair(alg, { extractable: true })

console.log('# public key')
console.log(await exportJWK(publicKey))

console.log('# private key')
console.log(await exportJWK(privateKey))

const payload = { user }

const jwt = await new SignJWT(payload)
    .setProtectedHeader({ alg })
    .setIssuer(issuer)
    .setExpirationTime('1d')
    .sign(privateKey)

console.log('# JWT')
console.log(jwt)
