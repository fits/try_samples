
const R = require('ramda')

const dup = R.curry((fn, x) => fn(x, x))

const perm2 = dup(R.pipe(
    R.xprod,
    R.filter(x => x[0] != x[1])
))

console.log( perm2(['A', 'B', 'C', 'D']) )
