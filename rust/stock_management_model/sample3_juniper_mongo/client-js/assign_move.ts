
import { request, gql } from 'graphql-request'

const endpoint = process.env['STOCKMOVE_ENDPOINT']

const id = process.argv[2]

const query = gql`
    mutation {
        assign(id: "${id}") {
            __typename
            id
            ... on AssignedStockMove {
                assigned
            }
        }
    }
`

request(endpoint, query)
    .then(r => console.log(r))
    .catch(err => console.error(err))
