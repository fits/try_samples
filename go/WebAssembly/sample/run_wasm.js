
const fs = require('fs')

const wasmFile = process.argv[2]

globalThis.crypto = {
    getRandomValues(b) {}
}

new Function(fs.readFileSync(`${process.env.GOROOT}/misc/wasm/wasm_exec.js`))()

const run = async () => {
    const go = new globalThis.Go()

    const wasm = await WebAssembly.instantiate(
        fs.readFileSync(wasmFile), 
        go.importObject
    )

    go.run(wasm.instance)
}

run().catch(err => console.error(err))
