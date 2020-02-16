
const R = require('ramda')

const data = {
    item: {
        name: 'item-1',
        details: {
            color: 'white',
            size: 'L'
        }
    },
    num: 5
}

const isObject = R.pipe(R.type, R.equals('Object'))

const valuesToObjOf = 
    R.pipe(
        R.mapObjIndexed(
            R.ifElse(
                isObject,
                v => valuesToObjOf(v), 
                R.flip(R.objOf)
            )
        ),
        R.values,
        R.flatten
    )

const flatObj = 
    R.pipe(
        valuesToObjOf,
        R.mergeAll
    )


console.log( valuesToObjOf(data) )

console.log('------')

console.log( flatObj(data) )
