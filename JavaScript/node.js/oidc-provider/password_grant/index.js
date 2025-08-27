import * as oidc from 'oidc-provider'

const port = 8080
const issuer = `http://127.0.0.1:${port}`

const users = [
    { id: 'u1', name: 'user1@example.com', password: 'pass1' }
]

const provider = new oidc.Provider(issuer, {
    ttl: {
        AccessToken: 90 * 24 * 60 * 60,
    },
    clients: [
        {
            client_id: 'test-client',
            client_secret: 'abc',
            grant_types: ['password'],
            redirect_uris: [
                'http://example.com/oidc/callback'
            ],
            response_types: [],
        }
    ],
})

const handler = async (ctx) => {
    const { params } = ctx.oidc

    const user = users.find(u => u.name === params.username && u.password === params.password)

    if (!user) {
        throw new oidc.errors.InvalidGrant('not found user')
    }

    const at = new ctx.oidc.provider.AccessToken({
        client: ctx.oidc.client,
        accountId: user.id,
    })

    const accessToken = await at.save()

    ctx.body = {
        access_token: accessToken,
        expire_in: at.expiration,
        token_type: at.tokenType,
    }
}

provider.registerGrantType('password', handler, ['username', 'password'])

provider.listen(port, () => {
    console.log(`started: port=${port}`)
})
