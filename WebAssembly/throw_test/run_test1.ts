import { check, ex1 } from './test1.wasm'

check(5)
console.log('5 ok')

check(10)
console.log('10 ok')

try {
    check(4)
    console.log('4 ok')
} catch(e) {
    console.log('4 error')
    console.log(e)

    console.log(`error arg=${e.getArg(ex1, 0)}`)
}

try {
    check(-1)
    console.log('-1 ok')
} catch(e) {
    console.log('-1 error')
    console.log(e)

    console.log(`error arg=${e.getArg(ex1, 0)}`)
}
