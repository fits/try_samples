
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

const valuesToObjOf = (obj, prefix = '') =>
    R.pipe(
        R.mapObjIndexed(
            R.ifElse(
                isObject,
                (v, k) => valuesToObjOf(v, `${prefix}${k}_`), 
                (v, k) => R.objOf(`${prefix}${k}`, v)
            )
        ),
        R.values,
        R.flatten
    )(obj)

const flatObj = 
    R.pipe(
        valuesToObjOf,
        R.mergeAll
    )


console.log( flatObj(data) )
