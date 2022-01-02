import { request, gql } from 'graphql-request'

const uri = process.env.GQL_URI ?? 'http://localhost:4000/graphql'

const print = (v: any) => console.log(JSON.stringify(v, null, 2))

const query = async (q: string, params: any = {}) => {
    return await request(uri, q, params)
}

const fragment = `
fragment ItemData on Item {
    id
    name
    value
}
`

const run = async () => {
    const r1 = await query(`
        ${fragment}
        query {
            items {
                edges {
                    ...ItemData
                }
                pageInfo {
                    total
                    size
                    from
                }
            }
        }
    `)

    print(r1)

    const r2 = await query(
        `
        ${fragment}
        query Items($size: Int!, $from: Int!) {
            items(size: $size, from: $from) {
                edges {
                    ...ItemData
                }
                pageInfo {
                    total
                    size
                    from
                }
            }
        }
        `,
        { 'size': 5, 'from': r1.items.pageInfo.size + r1.items.pageInfo.from }
    )

    print(r2)
}

run().catch(err => console.error(err))
