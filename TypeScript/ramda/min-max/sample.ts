import R from 'ramda'

type Compare = (a: any, b: any) => number

const sortHead = (field: string, f: Compare) => 
    R.pipe(
        R.map(R.prop(field)),
        R.filter(R.pipe(R.isNil, R.not)),
        R.sort(f),
        R.head
    )

const min = (field: string) => sortHead(field, R.ascend(R.identity))
const max = (field: string) => sortHead(field, R.descend(R.identity))

const data: any[] = [
    { price: 150 },
    { name: 'b2', price: 100 },
    { name: 'a1', price: 200, date: new Date('2022-01-31T19:00:00Z') },
    { name: 'c3', price: 120 },
    { price: 80, date: new Date('2022-02-01T03:45:00+09:00') },
    { name: 'a0', price: 120, date: new Date('2022-02-01T01:15:00+09:00') },
    { name: 'd4' },
]

const minName = min('name')
const maxName = max('name')

console.log(`name: min=${minName(data)}, max=${maxName(data)}`)

const minPrice = min('price')
const maxPrice = max('price')

console.log(`price: min=${minPrice(data)}, max=${maxPrice(data)}`)

const minDate = min('date')
const maxDate = max('date')

console.log(`date: min=${minDate(data)}, max=${maxDate(data)}`)

const minOther = min('other')

console.log(`other: min=${minOther(data)}`)
