
const R = require('ramda')

const obj = {
    name: 'item-1',
    details: {
        color: 'white',
        size: 1
    }
}

const obj2 = {
    item: {
        name: 'item-1',
        details: {
            color: 'white',
            size: 1
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


console.log( valuesToObjOf(obj) )
console.log( flatObj(obj) )

console.log('------')

console.log( valuesToObjOf(obj2) )
console.log( flatObj(obj2) )
