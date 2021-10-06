
import { createClient } from './opensearch_util'

const node = process.env.ES_ENDPOINT ?? 'http://localhost'
const index = 'items'

const run = async () => {
    const client = await createClient(node)

    const { body } = await client.search({
        index
    })

    console.log(JSON.stringify(body, null, 2))
}

run().catch(err => console.error(err))
