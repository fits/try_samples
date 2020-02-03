
const R = require('ramda')

const isObject = R.is(Object)
const isObject2 = R.pipe(R.type, R.equals('Object'))

const check = v => console.log(`${v} : isObject=${isObject(v)}, isObject2=${isObject2(v)}`)

check({})
check([1, 2])
check('a')
check(new String('a'))
check(new Date())
