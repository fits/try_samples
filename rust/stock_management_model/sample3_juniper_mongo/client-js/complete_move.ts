
import { request, gql } from 'graphql-request'

const endpoint = process.env['STOCKMOVE_ENDPOINT']

const id = process.argv[2]

const query = gql`
    mutation {
        complete(id: "${id}") {
            __typename
            id
        }
    }
`

request(endpoint, query)
    .then(r => console.log(r))
    .catch(err => console.error(err))
