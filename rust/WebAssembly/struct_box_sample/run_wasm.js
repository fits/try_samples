const fs = require('fs')

const wasmFile = process.argv[2]

const buf = fs.readFileSync(wasmFile)

WebAssembly.instantiate(buf, {})
    .then( ({ instance: wasm }) => {
        console.log(wasm.exports)

        const ptr = wasm.exports.new_data(123)
        console.log(`pointer: ${ptr}`)

        const v = wasm.exports.take_value(ptr)
        console.log(`value: ${v}`)

        const v2 = wasm.exports.take_value(ptr)
        console.log(`value: ${v2}`)

        wasm.exports.drop_data(ptr)

        //const v3 = wasm.exports.take_value(ptr)
        //console.log(`value: ${v3}`)
    })
    .catch(err => console.error(err))
