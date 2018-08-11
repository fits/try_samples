
const R = require('ramda')
const L = require('ramda-lens')

console.log( L.view(R.lensProp('value'), { value: 11 }) )

const value = R.lensProp('value')

const d1 = { name: 'data1', value: 1 }

const r1 = L.view(value, d1)

console.log(r1)

const d2 = L.set(value, 22, d1)

console.log(d2)

const d3 = L.over(value, x => x * 10, d1)

console.log(d3)
