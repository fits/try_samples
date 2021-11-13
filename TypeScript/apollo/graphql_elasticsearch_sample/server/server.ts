import { ApolloServer, gql } from 'apollo-server'
import { Client } from '@elastic/elasticsearch'

const host = process.env.ES_HOST ?? 'localhost'
const index = process.env.ES_INDEX ?? 'sample'

const node = `http://${host}:9200`
const client = new Client({ node })

const typeDefs = gql`
    type Category {
        code: ID!
        name: String!
        children: [Category!]
        countOfItems: Int!
    }

    type Query {
        categories: [Category!]!
    }
`

const isNotKeyword = (key: string) =>
    !['key', 'key_as_string', 'doc_count'].includes(key)

const fieldName = (rs: any) => {
    const ks = Object.keys(rs).filter(isNotKeyword)
    return ks.length > 0 ? ks[0] : undefined
}

const toDoc = (rs: any) => {
    const k1 = fieldName(rs)

    if (!k1) {
        return {}
    }

    const k2 = fieldName(rs[k1])!

    const bs = rs[k1][k2].buckets.map((b: any) => 
        Object.assign(
            {
                code: b.key[0],
                name: b.key[1],
                [k2]: b.doc_count
            },
            toDoc(b)
        )
    )

    return { 
        [k1]: bs
    }
}

const categories = async () => {
    const { body } = await client.search({
        index,
        body: {
            size: 0,
            aggs: {
                categories: {
                    nested: { path: 'categories' },
                    aggs: {
                        countOfItems: {
                            multi_terms: {
                                terms: [
                                    { field: 'categories.code' },
                                    { field: 'categories.name' }
                                ],
                                order: {
                                    _term: 'asc'
                                }
                            },
                            aggs: {
                                children: {
                                    nested: { path: 'categories.children' },
                                    aggs: {
                                        countOfItems: {
                                            multi_terms: {
                                                terms: [
                                                    { field: 'categories.children.code' },
                                                    { field: 'categories.children.name' }
                                                ],
                                                order: {
                                                    _term: 'asc'
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    })

    return toDoc(body.aggregations).categories
}

const run = async () => {
    const server = new ApolloServer({
        typeDefs,
        resolvers: {
            Query: {
                categories
            }
        }
    })

    const r = await server.listen()

    console.log(`server started: ${r.url}`)
}

run().catch(err => console.error(err))
