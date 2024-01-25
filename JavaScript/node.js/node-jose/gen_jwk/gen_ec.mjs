import jose from 'node-jose'
const { JWK } = jose

const includePrivateKey = process.argv.length > 2

const keystore = JWK.createKeyStore()

await keystore.generate('EC', 'P-256', {
    alg: 'ES256',
    use: 'sig'
})

const res = keystore.toJSON(includePrivateKey)

console.log(JSON.stringify(res, null, 2))
