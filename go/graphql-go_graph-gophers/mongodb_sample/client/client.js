
const { GraphQLClient, gql } = require('graphql-request')

const endpoint = 'http://localhost:8080/graphql'

const category = process.argv[2]
const value = parseInt(process.argv[3])

const q1 = gql`
    mutation CreateItem($category: Category!, $value: Int!) {
        create(input: { category: $category, value: $value }) {
            id
        }
    }
`

const q2 = gql`
    query FindItem($id: ID!) {
        find(id: $id) {
            id
            category
            value
        }
    }
`

const client = new GraphQLClient(endpoint)

const run = async () => {
    const r1 = await client.request(q1, { category, value })
    console.log(r1)

    const id = r1.create.id

    const r2 = await client.request(q2, { id })
    console.log(r2)
}

run().catch(err => console.error(err))
