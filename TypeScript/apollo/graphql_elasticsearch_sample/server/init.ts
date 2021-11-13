import { Client } from '@elastic/elasticsearch'

const N = 10

const host = process.env.ES_HOST ?? 'localhost'
const index = process.env.ES_INDEX ?? 'sample'

const node = `http://${host}:9200`
const client = new Client({ node })

const categories1 = ['A', 'B', 'C']
const categories2 = ['S', 'T', 'U']

const createIndex = async () => {
    const { body: exists } = await client.indices.exists({ index })

    if (exists) {
        await client.indices.delete({ index })
    }

    const { body } = await client.indices.create({
        index,
        body: {
            mappings: {
                dynamic_templates: [
                    {
                        string_keyword: {
                            match_mapping_type: 'string',
                            mapping: {
                                type: 'keyword'
                            }
                        }
                    }
                ],
                properties: {
                    categories: {
                        type: 'nested',
                        properties: {
                            children: {
                                type: 'nested'
                            }
                        }
                    }
                }
            }
        }
    })

    console.log(body)
}

const createData = async () => {
    for (let i = 0; i < N; i++) {
        const c1 = categories1[Math.round(Math.random() * (categories1.length - 1))]
        const c2 = categories2[Math.round(Math.random() * (categories2.length - 1))]

        const { body } = await client.index({
            index,
            body: {
                name: `item-${i + 1}`,
                categories: [
                    {
                        code: c1,
                        name: `Category${c1}`,
                        children: [
                            {
                                code: `${c1}-${c2}`,
                                name: `SubCategory${c1}${c2}`
                            }
                        ]
                    }
                ]
            }
        })

        console.log(body)
    }
}

const run = async () => {
    await createIndex()
    await createData()
}

run().catch(err => console.error(err))
