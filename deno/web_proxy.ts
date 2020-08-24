
import { serve } from 'https://deno.land/std@0.65.0/http/server.ts'
import { fromStreamReader } from 'https://deno.land/std@0.65.0/io/streams.ts'

const BASE_URL = Deno.env.get('PROXY_BASE_URL')

console.log(`baseUrl: ${BASE_URL}`)

const server = serve({ port: 8080 })

for await (const req of server) {
    const path = req.url
    const url = `${BASE_URL}${path}`

    console.log(`*** fetch: ${url}`)

    const res = await fetch(url)

    const status = res.status
    const headers = res.headers

    if (res.body) {
        const body = fromStreamReader(res.body.getReader())
        req.respond({ body, status, headers })
    }
    else {
        req.respond({ status, headers })
    }
}