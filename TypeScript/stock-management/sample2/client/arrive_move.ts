
import { request, gql } from 'graphql-request'

const endpoint = 'http://localhost:4000'

const id = process.argv[2]
const incoming = parseInt(process.argv[3])

const query = gql`
    mutation {
        arrive(id: "${id}", incoming: ${incoming}) {
            __typename
            id
            ... on ArrivedStockMove {
                incoming
            }
        }
    }
`

request(endpoint, query)
    .then(r => console.log(r))
    .catch(err => console.error(err))
