
const fs = require('fs')

const wasmFile = process.argv[2]

const run = async () => {
    const module = await WebAssembly.compile(fs.readFileSync(wasmFile))

    const imports = WebAssembly.Module.imports(module)

    console.log(imports)
}

run().catch(err => console.error(err))
