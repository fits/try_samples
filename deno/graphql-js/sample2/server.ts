import { graphql, buildSchema } from 'https://cdn.skypack.dev/graphql'

const port = 8080

const schema = buildSchema(`
    type Item {
        id: ID!
        value: Int!
    }

    type Query {
        find(id: ID!): Item
    }
`)

type FindInput = { id: string }

const rootValue = {
    find: ({ id }: FindInput) => {
        return { id, value: 123 }
    }
}

const handler = async (con: Deno.Conn) => {
    for await (const req of Deno.serveHttp(con)) {
        const source = await req.request.text()

        const res = await graphql({ schema, source, rootValue })

        console.log(res)

        await req.respondWith(new Response(JSON.stringify(res)))
    }
}

const server = Deno.listen({ port })

console.log(`listen: ${port}`)

for await (const con of server) {
    handler(con)
}
