
const R = require('ramda')

const data = [
    ['A'],
    ['A', 'B'],
    ['C', 'B', 'A'],
    ['E', 'A', 'B'],
    ['E', 'A', 'B', 'C'],
    ['D'],
    ['A', 'C'],
    ['B'],
    ['D', 'A'],
    ['E', 'B'],
    ['F'],
    ['A', 'C', 'B', 'E'],
    ['A', 'C', 'D', 'F']
]

const permN = R.curry((n, ds) => R.reduce(
    (a, b) => R.pipe(
        R.xprod,
        R.map(R.flatten),
        R.filter(x => R.uniq(x).length == b)
    )(a, ds), 
    ds, 
    R.range(2, n + 1)
))

const countPermN = R.curry((n, ds) =>
    R.pipe(
        R.map(permN(n)),
        R.reduce(R.concat, []),
        R.countBy(JSON.stringify),
        R.toPairs,
        R.map(x => R.append(x[1], JSON.parse(x[0])))
    )(ds)
)

console.log( countPermN(2, data) )

console.log('-----')

console.log( countPermN(3, data) )

console.log('-----')

const liftObjN = R.curry((n, fn, ds) =>
    R.reduce(
        (a, b) => R.mapObjIndexed(a), 
        fn, 
        R.range(0, n)
    )(ds)
)

const groupByMulti = R.curry((fs, ds) => R.reduce(
    (a, b) => liftObjN(b, R.groupBy(R.prop(fs[b])), a),
    ds, 
    R.range(0, fs.length)
))

const countPermNMap = R.curry((n, ds) =>
    R.pipe(
        countPermN,
        R.sortWith(
            R.concat(
                [ R.descend(R.nth(-1)) ],
                R.map(x => R.ascend(R.nth(x)), R.range(1, n))
            )
        ),
        groupByMulti(R.range(0, n)),
        liftObjN(n, R.pipe(R.head, R.nth(-1)))
    )(n, ds)
)

console.log( countPermNMap(2, data) )

console.log('-----')

console.log( countPermNMap(3, data) )

