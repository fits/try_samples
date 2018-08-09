
import * as R from 'ramda'

const t = R.reduce(R.add, 0, R.range(1, 10))

console.log(t)
