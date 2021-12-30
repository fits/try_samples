const wasmFile = Deno.args[0]

const run = async () => {
    const buf = await Deno.readFile(wasmFile)
    const module = await WebAssembly.compile(buf)

    let instance = null

    const importObject = {
        sample: {
            callback: (ptr, len) => {
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

    const query = (ptr, q) => {
        const buf = new TextEncoder('utf-8').encode(q)
        const sptr = instance.exports._new_string(buf.length)

        new Uint8Array(instance.exports.memory.buffer).set(buf, sptr)

        instance.exports.query(ptr, sptr, buf.length)

        instance.exports._drop_string(sptr)
    }

    const ptr = instance.exports.open()

    query(ptr, `
        mutation {
            create(input: { id: "item-1", value: 12 }) {
                id
            }
        }
    `)

    query(ptr, `
        mutation {
            create(input: { id: "item-2", value: 34 }) {
                id
            }
        }
    `)

    query(ptr, `
        query {
            find(id: "item-1") {
                id
                value
            }
        }
    `)

    query(ptr, `
        {
            find(id: "item-2") {
                id
                value
            }
        }
    `)

    query(ptr, `
        {
            find(id: "item-12") {
                id
            }
        }
    `)

    instance.exports.close(ptr)
}

run().catch(err => console.error(err))
