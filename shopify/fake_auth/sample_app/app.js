
const Koa = require('koa')
const session = require('koa-session')
const {default: shopifyAuth, verifyRequest} = require('@shopify/koa-shopify-auth')

const fetch = require('node-fetch')
globalThis.fetch = fetch

// setting for self signed certificate
process.env['NODE_TLS_REJECT_UNAUTHORIZED'] = 0

const port = 8081
const myShopifyDomain = '0.0.1:8580'

const {SHOPIFY_API_KEY, SHOPIFY_SECRET} = process.env

const app = new Koa()
app.keys = [SHOPIFY_SECRET]

app.use(session({}, app))

app.use(shopifyAuth({
    apiKey: SHOPIFY_API_KEY,
    secret: SHOPIFY_SECRET,
    scopes: ['write_products'],
    myShopifyDomain,
    afterAuth(ctx) {
        console.log('*** afterAuth')
        const {shop, accessToken} = ctx.session

        console.log(`shop = ${shop}`)
        console.log(`accessToken = ${accessToken}`)

        ctx.redirect('/')
    }
}))

app.use(verifyRequest())

app.use(ctx => {
    ctx.body = 'sample app'
})

app.listen(port)
