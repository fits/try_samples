
const wasmFile = Deno.args[0]

const run = async () => {
    const buf = await Deno.readFile(wasmFile)
    const module = await WebAssembly.compile(buf)

    const imports = WebAssembly.Module.imports(module)
    const exports = WebAssembly.Module.exports(module)

    console.log('--- imports ---')
    console.log(imports)

    console.log('--- exports ---')
    console.log(exports)

    console.log('--- call main ---')

    let instance = null

    const importObject = {
        sample: {
            log: (ptr, len) => {
                if (instance) {
                    const memory = instance.exports.memory.buffer

                    const buf = new Uint8Array(memory, ptr, len)
                    const msg = new TextDecoder('utf-8').decode(buf)

                    console.log(msg)
                }
            },
            message: () => {
                const msg = 'sample123'

                if (instance) {
                    const memory = instance.exports.memory.buffer

                    const buf = new TextEncoder('utf-8').encode(msg)
                    const ptr = instance.exports._new_string(buf.length)

                    new Uint8Array(memory).set(buf, ptr)

                    instance.exports._return_string(ptr, buf.length)

                    instance.exports._drop_string(ptr)
                }
            }
        }
    }

    instance = await WebAssembly.instantiate(module, importObject)
    instance.exports.main()
}

run().catch(err => console.error(err))
