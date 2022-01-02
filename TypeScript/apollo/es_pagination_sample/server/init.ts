import { Client } from '@elastic/elasticsearch'

const N = 100

const host = process.env.ES_HOST ?? 'localhost'
const index = process.env.ES_INDEX ?? 'items'

const node = `http://${host}:9200`
const client = new Client({ node })

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
                ]
            }
        }
    })

    console.log(body)
}

const createData = async () => {
    for (let i = 0; i < N; i++) {
        const value = Math.floor(Math.random() * 1000)

        const { body } = await client.index({
            index,
            body: {
                name: `item-${i + 1}`,
                value
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
