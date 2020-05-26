
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

    console.log('--- call main ---')

    let instance = null
    let counter = 50

    const importObject = {
        sample: {
            count_up: () => counter++,
            log: (ptr, len) => {
                if (instance) {
                    const memory = instance.exports.memory.buffer

                    const buf = new Uint8Array(memory, ptr, len)
                    const msg = new TextDecoder('utf-8').decode(buf)

                    console.log(msg)
                }
            }
        }
    }

    instance = await WebAssembly.instantiate(module, importObject)
    instance.exports.main()
}

run().catch(err => console.error(err))
