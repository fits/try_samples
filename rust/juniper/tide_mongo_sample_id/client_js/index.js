
const { request, gql } = require('graphql-request')

const url = 'http://localhost:8080/graphql'

const name = process.argv[2]
const value = parseInt(process.argv[3])

const query1 = gql`
mutation {
    create(input: { name: "${name}", value: ${value} }) {
        id
    }
}
`

const query2 = gql`
query findItem($id: ID!) {
    find(id: $id) {
        id
        name
        value
    }
}
`

const run = async () => {
    const res1 = await request(url, query1)

    console.log(res1)

    const id = res1.create.id

    const res2 = await request(url, query2, { id })

    console.log(res2)
}

run().catch(err => console.error(err))
