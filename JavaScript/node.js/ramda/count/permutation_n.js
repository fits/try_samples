
const R = require('ramda')

const permN = R.curry((n, ds) => R.reduce(
    (a, b) => R.pipe(
        R.xprod,
        R.map(R.flatten),
        R.filter(x => R.uniq(x).length == b)
    )(a, ds), 
    ds, 
    R.range(2, n + 1)
))

console.log( permN(1, ['A', 'B']) )

console.log('----------')

console.log( permN(2, ['A']) )

console.log('----------')

console.log( permN(2, ['A', 'B']) )
console.log('----------')
console.log( permN(2, ['A', 'B', 'C']) )

console.log('----------')

console.log( permN(3, ['A', 'B', 'C']) )
console.log('----------')
console.log( permN(3, ['A', 'B', 'C', 'D']) )
