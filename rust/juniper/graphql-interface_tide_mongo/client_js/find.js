
const { request, gql } = require('graphql-request')

const url = 'http://localhost:8080/graphql'

const id = process.argv[2]

const query = gql`
query findEvent($id: ID!) {
    find(id: $id) {
        __typename
        id
        target
        ... on Created {
            value
        }
        ... on Deleted {
            reason
        }
    }
}
`
const run = async () => {
    const res = await request(url, query, { id })

    console.log(res)
}

run().catch(err => console.error(err))
