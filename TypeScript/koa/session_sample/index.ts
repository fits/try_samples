
import * as session from 'koa-session'
import * as Koa from 'koa'

const app = new Koa()

const storeData = {}

const store = {
    async get(key, maxAge, _) {
        console.log(`*** get: ${key}`)
        return storeData[key]
    },
    async set(key, sess, maxAge, _) {
        console.log(`*** set: ${key}, ${JSON.stringify(sess)}`)
        storeData[key] = sess
    },
    async destroy(key, _) {
        console.log(`*** destory: ${key}`)
        storeData[key] = undefined
    }
} as session.stores

const config = {
    signed: false,
    valid(ctx, sess) {
        console.log(`*** valid: ${JSON.stringify(sess)}`)
        return true
    },
    beforeSave(ctx, sess) {
        console.log(`*** beforeSave: ${JSON.stringify(sess)}`)
    }, 
    store
} as Partial<session.opts>

app.use(session(config, app))

app.use(ctx => {
    const c = ctx.session.counter || 0

    ctx.session.counter = c + 1
    ctx.body = `counter-${ctx.session.counter}`
})

app.listen(8080)

console.log('start localhost:8080')