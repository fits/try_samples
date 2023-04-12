import { serve } from 'https://deno.land/std@0.182.0/http/server.ts'

serve(req => {
    req.headers.forEach((v, k) => console.log(`header ${k}:${v}`))
    console.log('-----')

    return new Response('ok')

},{ port: 8080 })
