
import { request, gql } from 'graphql-request'

const endpoint = process.env['STOCKMOVE_ENDPOINT']

const id = process.argv[2]

const query = gql`
    {
        findMove(id: "${id}") {
            __typename
            id
            info {
                item
                qty
                from
                to
            }
            ... on AssignedStockMove {
                assigned
            }
            ... on ShippedStockMove {
                outgoing
            }
            ... on ArrivedStockMove {
                outgoing
                incoming
            }
            ... on CompletedStockMove {
                outgoing
                incoming
            }
        }
    }
`

request(endpoint, query)
    .then(r => console.log(r))
    .catch(err => console.error(err))
