
import { request, gql } from 'graphql-request'

const endpoint = 'http://localhost:4000'

const item = process.argv[2]
const location = process.argv[3]

const query = gql`
    {
        findStock(item: "${item}", location: "${location}") {
            __typename
            item
            location
            ... on ManagedStock {
                qty
                assigned
            }
        }
    }
`

request(endpoint, query)
    .then(r => console.log(r))
    .catch(err => console.error(err))
