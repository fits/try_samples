import Provider from 'oidc-provider'

const port = 8080

const config = {
    ttl: {
        AccessToken: 90 * 24 * 60 * 60,
    },
    clients: [
        {
            client_id: 'test1',
            client_secret: 'secret1',
            grant_types: ['sample'],
            response_types: [],
        }
    ]
}

const provider = new Provider(`http://localhost:${port}`, config)

const handler = async (ctx, next) => {
    const token = new provider.AccessToken({
        client: ctx.oidc.client,
        accountId: 'u1'
    })

    ctx.body = {
        access_token: await token.save(),
        expire_in: token.expiration,
        token_type: 'Bearer',
    }

    await next()
}

provider.registerGrantType('sample', handler)

provider.listen(port, () => {
    console.log(`started: port=${port}`)
})
