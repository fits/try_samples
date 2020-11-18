
const { request, gql } = require('graphql-request')

const uri = 'http://localhost:8080/graphql'

const run = async () => {
    const r1 = await request(uri, gql`
        {
            find(id: "invalid-id") {
                id
                category
                value
            }
        }
    `)

    console.log(r1)

    const r2 = await request(uri, gql`
        mutation {
            create(input: { category: Extra, value: 123 }) {
                id
            }
        }
    `)

    console.log(r2)

    const id = r2.create.id

    const r3 = await request(uri, gql`
        {
            find(id: "${id}") {
                id
                category
                value
            }
        }
    `)

    console.log(r3)

    const r4 = await request(
        uri, 
        gql`
            query findItem($id: ID!) {
                find(id: $id) {
                    id
                    category
                    value
                }
            }
        `, 
        { id }
    )

    console.log(r4)
}

run().catch(err => console.error(err))
