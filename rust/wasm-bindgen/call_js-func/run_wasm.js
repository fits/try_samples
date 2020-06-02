
global.func1 = v => console.log(`called func1: ${v}`)

const wasm = require('./pkg/call_js_func.js')

wasm.run('sample')
