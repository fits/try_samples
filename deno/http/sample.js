
import { serve } from 'https://deno.land/std@0.64.0/http/server.ts'

const server = serve({ port: 8080 })

for await (const req of server) {
    console.log(req)
    req.respond({ body: 'sample data' })
}
