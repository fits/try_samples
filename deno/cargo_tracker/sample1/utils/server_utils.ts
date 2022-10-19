
type Processor = (src: string) => Promise<string>

export const serve = async (process: Processor, port = 8080) => {

    const handler = async (con: Deno.Conn) => {
        for await (const req of Deno.serveHttp(con)) {
            const source = await req.request.text()
    
            const res = await process(source)
    
            await req.respondWith(new Response(res))
        }
    }
    
    const server = Deno.listen({ port })
    
    for await (const con of server) {
        handler(con)
    }
}
