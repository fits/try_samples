
import { request, gql } from 'graphql-request'

const endpoint = 'http://localhost:4000'

const id = process.argv[2]

const query = gql`
    mutation {
        cancel(id: "${id}") {
            __typename
            id
        }
    }
`

request(endpoint, query)
    .then(r => console.log(r))
    .catch(err => console.error(err))
