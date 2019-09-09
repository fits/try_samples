
const R = require('ramda')

const data = [
    { date: '2019-09-01', item: 'A', value: 10 },
    { date: '2019-09-01', item: 'B', value: 5 },
    { date: '2019-09-02', item: 'A', value: 7 },
    { date: '2019-09-02', item: 'B', value: 3 },
    { date: '2019-09-03', item: 'A', value: 12 },
    { date: '2019-09-03', item: 'B', value: 8 },
    { date: '2019-09-04', item: 'A', value: 4 },
    { date: '2019-09-04', item: 'B', value: 10 },
    { date: '2019-09-05', item: 'A', value: 5 },
    { date: '2019-09-05', item: 'B', value: 2 },
    { date: '2019-09-06', item: 'A', value: 1 },
    { date: '2019-09-06', item: 'B', value: 8 }
]

const logResult = (a, b, c) => 
    console.log(`mean = ${a}, median = ${b}, sum = ${c}`)

const f = R.pipe(
    R.map(R.prop('value')),
    R.juxt([R.mean, R.median, R.sum]),
    R.apply(logResult)
)

f(data)
