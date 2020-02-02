
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


const valuesToObjOf = 
    R.mapObjIndexed(
        R.ifElse(
            R.is(Object),
            R.pipe(
                v => valuesToObjOf(v), 
                R.values
            ),
            R.flip(R.objOf)
        )
    )

const flatObj = 
    R.pipe(
        valuesToObjOf,
        R.values,
        R.flatten,
        R.mergeAll
    )


console.log( valuesToObjOf(obj) )
console.log( flatObj(obj) )

console.log('------')

console.log( valuesToObjOf(obj2) )
console.log( flatObj(obj2) )
