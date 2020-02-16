
const R = require('ramda')

const { Client } = require('@elastic/elasticsearch')

const esClient = new Client({ node: 'http://localhost:9200' })

const esIndex = 'samples'

const init = async () => {
    await Promise.all(
        R.map(
            ([i, j]) => esClient.index({
                index: esIndex,
                id: `${i}-${j}`,
                body: {
                    item: {
                        code: `item-${i}`
                    },
                    location: {
                        code: `loc-${j}`
                    }
                },
                refresh: true
            }),
            R.xprod(R.range(1, 3), R.range(1, 5))
        )
    )
}

const toSource = R.pipe(
    R.pathOr([], ['body', 'hits', 'hits']),
    R.map(R.prop('_source'))
)

const termsSearch = async (itemCode, locationCodes) => {
    const res = await esClient.search({
        index: esIndex,
        body: {
            query: {
                bool: {
                    filter: [
                        { term: { 'item.code.keyword': itemCode }},
                        { terms: {
                            'location.code.keyword': R.flatten([locationCodes])
                        }}
                    ]
                }
            },
            sort: [ 'location.code.keyword' ]
        }
    })

    return toSource(res)
}


const run = async () => {
    await init()

    const res1 = await termsSearch('item-1', 'loc-3')

    console.log(res1)

    const res2 = await termsSearch('item-2', ['loc-2', 'loc-3'])

    console.log(res2)
}

run().catch(err => console.error(err))
