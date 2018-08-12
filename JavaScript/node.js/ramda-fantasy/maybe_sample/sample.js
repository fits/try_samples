
const Maybe = require('ramda-fantasy').Maybe

const d1 = Maybe.Just(5)

console.log(d1)

const d2 = d1.chain(v => Maybe.Just(v * 10))

console.log(d2)

const d3 = d1.chain(a => d2.map(b => a + b))

console.log(d3)

const d4 = Maybe.Just(v => v * 20).ap(d1)

console.log(d4.getOrElse(-1))

const d5 = Maybe.Nothing().chain(v => Maybe.Just(v * 3))

console.log(d5.getOrElse(-1))
