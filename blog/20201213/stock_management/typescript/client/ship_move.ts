
import { request, gql } from 'graphql-request'

const endpoint = 'http://localhost:4000'

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
