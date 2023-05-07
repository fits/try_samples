import { serve } from 'https://deno.land/std@0.182.0/http/server.ts'

const port = parseInt(Deno.env.get('PORT') ?? '8100')

const handler = (req: Request) => {
    req.headers.forEach((v, k) => console.log(`header ${k}:${v}`))
    console.log('-----')

    const v = req.headers.get('authorization') ?? ''

    if (v === 'abc123') {
        return new Response(undefined, { status: 200 })
    }

    return new Response(undefined, { status: 403 })
}

serve(handler, { port })
