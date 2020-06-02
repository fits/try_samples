
const wasm = require('./pkg/export_rust_type.js')

console.log(wasm)

const d = new wasm.Data('sample1', 10)

console.log(d)

console.log(`name: ${d.name}, value: ${d.value}`)

console.log(d.show())

console.log('-----')

const d2 = new wasm.Data2('sample2', 20)

console.log(d2)
console.log(d2.toJSON())
