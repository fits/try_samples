import { ApolloServer, gql } from 'apollo-server'
import { Client } from '@elastic/elasticsearch'

const host = process.env.ES_HOST ?? 'localhost'
const index = process.env.ES_INDEX ?? 'items'
const node = `http://${host}:9200`

const typeDefs = `
    type PageInfo {
        total: Int!
        size: Int!
        from: Int!
    }

    type Item {
        id: ID!
        name: String!
        value: Int!
    }

    type ItemConnection {
        edges: [Item!]!
        pageInfo: PageInfo!
    }

    type Query {
        items(size: Int, from: Int): ItemConnection!
    }
`

const resolvers = {
    Query: {
        items: async (_parent, { size, from }, { client }) => {
            size = size ?? 10
            from = from ?? 0

            const { body } = await client.search({
                index,
                from,
                size
            })

            const edges = body.hits.hits.map(r => 
                Object.assign(r._source, { id: r._id })
            )

            return {
                edges,
                pageInfo: {
                    total: body.hits.total.value,
                    size,
                    from
                }
            }
        }
    }
}

const run = async () => {
    const client = new Client({ node })

    const server = new ApolloServer({
        typeDefs,
        resolvers,
        context: {
            client
        }
    })

    const r = await server.listen()

    console.log(`started: ${r.url}`)
}

run().catch(err => console.error(err))
