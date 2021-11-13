import { request, gql } from 'graphql-request'

const uri = process.env.GQL_URI ?? 'http://localhost:4000/graphql'

const run = async () => {
    const r = await request(
        uri, 
        gql`
            fragment CategoryData on Category {
                code
                name
                countOfItems
            }

            query {
                categories {
                    ...CategoryData
                    children {
                        ...CategoryData
                    }
                }
            }
        `
    )

    console.log(JSON.stringify(r, null, 2))
}

run().catch(err => console.error(err))
