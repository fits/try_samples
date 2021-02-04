
const { request, gql } = require('graphql-request')

const url = 'http://localhost:8080/graphql'

const target = process.argv[2]
const value = parseInt(process.argv[3])

const query = gql`
mutation {
    create(input: { target: "${target}", value: ${value} }) {
        id
    }
}
`

const run = async () => {
    const res = await request(url, query)

    console.log(res)
}

run().catch(err => console.error(err))
