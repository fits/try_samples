const wasmFile = Deno.args[0]

const run = async () => {
    const buf = await Deno.readFile(wasmFile)
    const module = await WebAssembly.compile(buf)

    let instance = await WebAssembly.instantiate(module, {})

    const toResult = ptr => {
        const sptr = instance.exports._result_ptr(ptr) 
        const len = instance.exports._result_size(ptr)

        const memory = instance.exports.memory.buffer

        const buf = new Uint8Array(memory, sptr, len)
        const res = new TextDecoder('utf-8').decode(buf)

        instance.exports._drop_result(ptr)

        return JSON.parse(res)
    }

    const query = (ptr, q) => {
        const buf = new TextEncoder('utf-8').encode(q)
        const sptr = instance.exports._new_string(buf.length)

        new Uint8Array(instance.exports.memory.buffer).set(buf, sptr)

        const r = toResult(
            instance.exports.query(ptr, sptr, buf.length)
        )

        instance.exports._drop_string(sptr)

        return r
    }

    const ptr = instance.exports.open()

    console.log(
        query(ptr, `
            mutation {
                create(input: { id: "item-1", value: 12 }) {
                    id
                }
            }
        `)
    )

    console.log(
        query(ptr, `
            mutation {
                create(input: { id: "item-2", value: 34 }) {
                    id
                }
            }
        `)
    )

    console.log(
        query(ptr, `
            query {
                find(id: "item-1") {
                    id
                    value
                }
            }
        `)
    )

    console.log(
        query(ptr, `
            {
                find(id: "item-2") {
                    id
                    value
                }
            }
        `)
    )

    console.log(
        query(ptr, `
            {
                find(id: "item-12") {
                    id
                }
            }
        `)
    )

    instance.exports.close(ptr)
}

run().catch(err => console.error(err))
