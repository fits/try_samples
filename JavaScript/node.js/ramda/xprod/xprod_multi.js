
const R = require('ramda')

const xprodN = R.reduce(
    (acc, v) => R.isEmpty(acc) ? v : R.map(R.flatten, R.xprod(acc, v)),
    []
)

console.log( xprodN([[1, 2]]) )

console.log( xprodN([[1, 2], ['a', 'b', 'c']]) )

console.log( xprodN([[1, 2], ['a', 'b', 'c'], [10, 20]]) )

console.log( xprodN([[1, 2], ['a', 'b', 'c'], [10, 20], ['x']]) )
