
const fs = require('fs')

const wasmFile = process.argv[2]

const run = async () => {
    const module = await WebAssembly.compile(fs.readFileSync(wasmFile))

    const imports = WebAssembly.Module.imports(module)
    const exports = WebAssembly.Module.exports(module)

    console.log('--- imports ---')
    console.log(imports)

    console.log('--- exports ---')
    console.log(exports)
}

run().catch(err => console.error(err))