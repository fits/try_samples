
import { Client } from '@elastic/elasticsearch'

const host = process.env.ELASTICSEARCH ?? 'localhost'
const node = `http://${host}:9200`

const indexName = 'items'

const client = new Client({ node })

const createIndex = async () => {
    const { body: exists } = await client.indices.exists({ index: indexName })

    if (!exists) {
        const r = await client.indices.create({
            index: indexName,
            body: {
                mappings: {
                    properties: {
                        name: { type: 'text' },
                        variations: {
                            type: 'nested',
                            properties: {
                                color: { type: 'keyword' },
                                stock: { type: 'integer' }
                            }
                        }
                    }
                }
            }
        })

        console.log(r)
    }
}

const addItem = async (id: string, name: string) => {
    const { body } = await client.create({
        index: indexName,
        id,
        body: {
            name,
            variations: []
        }
    })

    return body
}

const addVariation = async (id: string, color: string) => {
    const { body } = await client.update({
        index: indexName,
        id,
        body: {
            script: {
                source: 'ctx._source.variations.add(params)',
                params: {
                    color,
                    stock: 0
                }
            }
        }
    })

    return body
}

const updateStock = async (id: string, color: string, stock: number) => {
    const { body } = await client.update({
        index: indexName,
        id,
        body: {
            script: {
                source: 'for(v in ctx._source.variations) { if (v.color == params.color) { v.stock = params.stock } }',
                params: {
                    color,
                    stock
                }
            }
        }
    })

    return body
}

const searchColorInStock = async (color: string) => {
    const { body } = await client.search({
        index: indexName,
        body: {
            query: {
                nested: {
                    path: 'variations',
                    query: {
                        bool: {
                            must: [
                                { term: { 'variations.color': color } },
                                { range: { 'variations.stock': { gt: 0 } } }
                            ]
                        }
                    }
                }
            }
        }
    })

    return body.hits.hits
}

const run = async () => {
    await createIndex()

    const id = `id-${Date.now()}`
 
    const r1 = await addItem(id, `item-${Date.now()}`)
    console.log(r1)

    const r2 = await addVariation(id, 'white')
    console.log(r2)

    const r3 = await addVariation(id, 'black')
    console.log(r3)

    const r4 = await updateStock(id, 'black', 5)
    console.log(r4)

    const r5 = await searchColorInStock('black')
    console.log(r5)
}

run().catch(err => console.error(err))