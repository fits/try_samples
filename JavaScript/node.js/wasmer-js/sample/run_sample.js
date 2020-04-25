
const fs = require('fs')
const { WASI } = require('@wasmer/wasi')

const wasmFile = process.argv[2]

const wasi = new WASI()

const run = async () => {
    const module = await WebAssembly.compile(fs.readFileSync(wasmFile))

	const importObject = wasi.getImports(module)

    const instance = await WebAssembly.instantiate(module, importObject)

    wasi.start(instance)
}

run().catch(err => console.error(err))