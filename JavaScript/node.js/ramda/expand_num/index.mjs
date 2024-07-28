import * as R from 'ramda'

const expand_num = (numProp, ds) => ds.flatMap((x, i) =>
    [...Array(x[numProp]).keys()].map(j => Object.assign({ index: [i, j] }, x))
)

const mapIndexed = R.addIndex(R.map)

const expand_num1 = (numProp) => R.pipe(
    mapIndexed((x, i) =>
        R.mergeLeft(x, { index: R.xprod([i], R.range(0, R.prop(numProp, x))) })
    ),
    R.chain(R.unwind('index')),
)

const d1 = [
    { id: 'a1', qty: 2 },
    { id: 'b2', qty: 1 },
    { id: 'c3', qty: 3 },
    { id: 'd4', qty: 0 },
]

const r1 = expand_num('qty', d1)

console.log(r1)

const r2 = expand_num1('qty')(d1)

console.log(r2)
