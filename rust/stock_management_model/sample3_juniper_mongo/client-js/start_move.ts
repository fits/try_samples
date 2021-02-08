
import { request, gql } from 'graphql-request'

const endpoint = process.env['STOCKMOVE_ENDPOINT']

const item = process.argv[2]
const qty = parseInt(process.argv[3])
const from = process.argv[4]
const to = process.argv[5]

const query = gql`
    mutation {
        start(input: { item: "${item}", qty: ${qty}, from: "${from}", to: "${to}" }) {
            __typename
            id
            info {
                item
                qty
                from
                to
            }
        }
    }
`

request(endpoint, query)
    .then(r => console.log(r))
    .catch(err => console.error(err))
