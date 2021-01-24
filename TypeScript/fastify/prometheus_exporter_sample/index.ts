
import Fastify from 'fastify'

const app = Fastify({})

const random = () => Math.floor(Math.random() * 30)

app.get('/metrics', async (req, res) => {
    console.log('*** metrics')

    const a = random()
    const b = random()
    const c = random()

    const ms = [
        `sample_metric{label="a"} ${a}`,
        `sample_metric{label="b"} ${b}`,
        `sample_metric{label="c"} ${c}`,
        `sample_metric_total ${a + b + c}`,
    ]

    return ms.join('\n')
})

const run = async () => {
    await app.listen(8080)

    const adr = app.server.address()
    console.log(`started: ${adr.address}:${adr.port}`)
}

run().catch(err => console.error(err))
