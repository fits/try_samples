
import { serve } from 'https://deno.land/std@0.65.0/http/server.ts'

const server = serve({ port: 8080 })

for await (const req of server) {
    //console.log(req)
    
    const buf = await Deno.readAll(req.body)
    const body = new TextDecoder().decode(buf)
    
    console.log(body)
    
    req.respond({ body: 'sample data' })
}
