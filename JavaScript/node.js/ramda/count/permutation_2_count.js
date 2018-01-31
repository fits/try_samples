
const R = require('ramda')

const data = [
    ['A'],
    ['A', 'B'],
    ['C', 'B', 'A'],
    ['E', 'A', 'B'],
    ['D'],
    ['A', 'C'],
    ['B'],
    ['D', 'A'],
    ['E', 'B'],
    ['F']
]

const dup = R.curry((fn, x) => fn(x, x))

const perm2 = dup(R.pipe(
    R.xprod,
    R.filter(x => x[0] != x[1])
))

const countPerm2 = R.pipe(
    R.map(perm2),
    R.reduce(R.concat, []),
    R.countBy(JSON.stringify),
    R.toPairs,
    R.map(x => R.append(x[1], JSON.parse(x[0]))),
    R.map(R.zipObj(['item1', 'item2', 'count']))
)

console.log( countPerm2(data) )

console.log('-----')

const liftObjN = R.curry((n, fn, data) =>
    R.reduce(
        (a, b) => R.mapObjIndexed(a), 
        fn, 
        R.range(0, n)
    )(data)
)

const groupByMulti = R.curry((fs, data) => R.reduce(
    (a, b) => liftObjN(b, R.groupBy(R.prop(fs[b])), a),
    data, 
    R.range(0, fs.length)
))

const countPerm2Map = R.pipe(
    countPerm2,
    R.sortWith([
        R.descend(R.prop('count')), 
        R.ascend(R.prop('item2'))
    ]),
    groupByMulti(['item1', 'item2']), 
    liftObjN(2, R.pipe(R.head, R.prop('count')))
)

console.log( countPerm2Map(data) )
