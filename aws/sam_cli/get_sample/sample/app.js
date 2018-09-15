'use strict'

const R = require('ramda')
const M = require('ramda-fantasy').Maybe

const items = [
    { id: 'a1', name: 'itemA1' },
    { id: 'b2', name: 'itemB2' },
    { id: 'c3', name: 'itemC3' }
]

const findItem = id =>
    M.toMaybe(
        R.find(R.propEq('id', id))(items)
    )

const pathId = R.path(['pathParameters', 'id'])

const body = d => new Object({
    statusCode: 200,
    body: JSON.stringify(d)
})

exports.handler = async (event, context) => {
    console.log(`*** ${process.env.PARAM1}`)

    console.log(event)

    const find = R.ifElse(
        R.pipe(pathId, R.isNil),
        x => M.of(items),
        R.pipe(pathId, findItem)
    )

    return find(event)
            .map(body)
            .getOrElse(new Error('not found'))
}
