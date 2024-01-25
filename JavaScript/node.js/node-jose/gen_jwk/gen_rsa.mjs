import jose from 'node-jose'
const { JWK } = jose

const includePrivateKey = process.argv.length > 2

const keystore = JWK.createKeyStore()

await keystore.generate('RSA', 2048, {
    alg: 'RS256',
    use: 'sig'
})

const res = keystore.toJSON(includePrivateKey)

console.log(JSON.stringify(res, null, 2))
