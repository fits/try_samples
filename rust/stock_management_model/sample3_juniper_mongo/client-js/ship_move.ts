
import { request, gql } from 'graphql-request'

const endpoint = process.env['STOCKMOVE_ENDPOINT']

const id = process.argv[2]
const outgoing = parseInt(process.argv[3])

const query = gql`
    mutation {
        ship(id: "${id}", outgoing: ${outgoing}) {
            __typename
            id
            ... on ShippedStockMove {
                outgoing
            }
        }
    }
`

request(endpoint, query)
    .then(r => console.log(r))
    .catch(err => console.error(err))
