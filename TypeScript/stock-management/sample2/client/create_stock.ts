
import { request, gql } from 'graphql-request'

const endpoint = 'http://localhost:4000'

const item = process.argv[2]
const location = process.argv[3]

const q1 = gql`
    mutation CreateUnmanaged($item: ID!, $location: ID!) {
        createUnmanaged(input: { item: $item, location: $location }) {
            __typename
            item
            location
        }
    }
`

const q2 = gql`
    mutation CreateManaged($item: ID!, $location: ID!) {
        createManaged(input: { item: $item, location: $location }) {
            __typename
            item
            location
        }
    }
`

const query = process.argv.length > 4 ? q1 : q2

request(endpoint, query, { item, location })
    .then(r => console.log(r))
    .catch(err => console.error(err))
