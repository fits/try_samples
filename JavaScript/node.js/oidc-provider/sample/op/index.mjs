import Provider from 'oidc-provider'
import jose from 'node-jose'

const { JWK } = jose

const port = 8000

const keystore = JWK.createKeyStore()

await keystore.generate('RSA', 2048, {
    alg: 'RS256',
    use: 'sig'
})

const config = {
    clients: [
        {
            client_id: 'sample1',
            client_secret: 'secret1',
            redirect_uris: ['https://127.0.0.1:3000/cb'],
            response_types: ['id_token'],
            grant_types: ['implicit'],

        }
    ],
    jwks: keystore.toJSON(true),
    claims: {
        user: null,
        store: null,
    },
    features: {
        claimsParameter: {
            enabled: true
        }
    },
    async findAccount(_ctx, id) {
        console.log(`findAccount: ${id}`)

        return {
            accountId: id,
            async claims(_use, _scope) {
                return { sub: id, user: 'u-1', store: 's-123' }
            }
        }
    }
}

const oidc = new Provider(`http://127.0.0.1:${port}`, config)

oidc.listen(port, () => {
    console.log(`started: port=${port}`)
})
