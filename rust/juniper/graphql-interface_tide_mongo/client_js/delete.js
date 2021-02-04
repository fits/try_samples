
const { request, gql } = require('graphql-request')

const url = 'http://localhost:8080/graphql'

const target = process.argv[2]
const reason = process.argv.length > 3 ? process.argv[3] : null

const query = gql`
mutation DeleteEvent($target: ID!, $reason: String) {
    delete(input: { target: $target, reason: $reason }) {
        id
    }
}
`

const run = async () => {
    const res = await request(url, query, { target, reason })

    console.log(res)
}

run().catch(err => console.error(err))
