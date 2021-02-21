
const session = require('koa-session')
const Koa = require('koa')

const app = new Koa()
app.keys = ['sample']

const storeData = {}

const store = {
    async get(key, maxAge, { rolling, ctx }) {
        console.log(`*** get: ${key}`)
        return storeData[key]
    },
    async set(key, sess, maxAge, { rolling, changd, ctx }) {
        console.log(`*** set: ${key}, ${JSON.stringify(sess)}`)
        storeData[key] = sess
    },
    async destroy(key, { ctx }) {
        console.log(`*** destory: ${key}`)
        storeData[key] = undefined
    }
}

const config = {
    valid(ctx, sess) {
        console.log(`*** valid: ${JSON.stringify(sess)}`)
        return true
    },
    beforeSave(ctx, sess) {
        console.log(`*** beforeSave: ${JSON.stringify(sess)}`)
    }, 
    store
}

app.use(session(config, app))

app.use(ctx => {
    const c = ctx.session.counter || 0

    ctx.session.counter = c + 1
    ctx.body = `counter-${ctx.session.counter}`
})

app.listen(8080)

console.log('start localhost:8080')