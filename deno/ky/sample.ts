
import ky from 'https://deno.land/x/ky/index.js'

const api = ky.create({prefixUrl: 'http://localhost:8080'})

const run = async () => {
    const r1 = await api.get('').text()

    console.log(r1)

    const r2 = await api.post('stocks', {
        json: { qty: 10 }
    }).json()

    console.log(r2)
}

run().catch(err => console.error(err))
