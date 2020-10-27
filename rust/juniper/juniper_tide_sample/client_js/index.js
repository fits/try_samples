
const { request, gql } = require('graphql-request')

const url = 'http://localhost:8080/graphql'

const query1 = gql`
mutation {
    create(input: { name: "item-1", value: 1 }) {
        id
    }
}
`

const query2 = gql`
query findItem($id: String!) {
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
