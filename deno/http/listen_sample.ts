
const port = 8080

const handle = async (con: Deno.Conn) => {
    for await (const req of Deno.serveHttp(con)) {
        console.log(req.request.url)
        await req.respondWith(new Response('ok'))
    }
}

const server = Deno.listen({ port })

console.log(`listen: ${port}`)

for await (const con of server) {
    handle(con)
}
