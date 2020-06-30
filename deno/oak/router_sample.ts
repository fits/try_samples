
import {Application, Router, Status} from 'https://deno.land/x/oak/mod.ts'

const router = new Router()

router.get('/', ctx => {
    ctx.response.body = 'sample data'
})

router.post('/stocks', async (ctx) => {
    const body = await ctx.request.body()

    if (body.type != 'json') {
        ctx.throw(Status.BadRequest, 'Content-Type is not application/json')
    }

    const v = body.value

    console.log(v)

    v.id = 'data1'

    ctx.response.type = 'json'
    ctx.response.body = v
})

const app = new Application()

app.use(router.routes())
app.use(router.allowedMethods())

app.addEventListener('listen', ({ port }) => {
    console.log(`started: port = ${port}`)
})

app.listen({port: 8080})
