import { checkValue, valueError } from './throw1.wasm'

checkValue(5)
console.log('ok 5')

try {
    checkValue(4)
    console.log('ok 4')
} catch(e) {
    console.log('err 4')
    console.log(e)

    if (e instanceof WebAssembly.Exception) {
        console.log(`error param1=${e.getArg(valueError, 0)}`)
    }
}
