
import { bindGql } from '../utils/graphql_utils.ts'
import { serve } from '../utils/server_utils.ts'

import { locations, UnLocode } from './sample_locations.ts'

const port = parseInt(Deno.env.get('LOCATION_PORT') ?? '8081')

const schema = `
    type Location {
        unLocode: ID!
        name: String!
    }

    type Query {
        find(unLocode: ID!): Location
    }
`

type FindInput = { unLocode: UnLocode }

const rootValue = {
    find: ({ unLocode }: FindInput) => locations.find(d => d.unLocode === unLocode)
}

const gql = bindGql(schema, rootValue)

const proc = async (req: string) => JSON.stringify(await gql(req))

serve(proc, port)

console.log(`listen: ${port}`)
