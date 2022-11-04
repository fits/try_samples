
import { gqlServe } from '../utils/graphql_utils.ts'

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

gqlServe({ schema, rootValue, port })

console.log(`listen: ${port}`)
