const wasmFile = 'target/wasm32-unknown-unknown/release/sample.wasm'

const toResult = (wasm, retptr) => {
    const sptr = wasm.exports._result_ptr(retptr) 
    const len = wasm.exports._result_size(retptr)

    const memory = wasm.exports.memory.buffer

    const buf = new Uint8Array(memory, sptr, len)
    const res = new TextDecoder('utf-8').decode(buf)

    return JSON.parse(res)
}

const query = (wasm, ptr, q) => {
    const buf = new TextEncoder('utf-8').encode(q)
    const sptr = wasm.exports._new_string(buf.length)

    try {
        new Uint8Array(wasm.exports.memory.buffer).set(buf, sptr)

        const retptr = wasm.exports.query(ptr, sptr, buf.length)

        try {
            return toResult(wasm, retptr)
        } finally {
            wasm.exports._drop_result(retptr)
        }
    } finally {
        wasm.exports._drop_string(sptr)
    }
}

const run = async () => {
    const buf = await Deno.readFile(wasmFile)
    const module = await WebAssembly.compile(buf)

    const wasm = await WebAssembly.instantiate(module, {})

    const ctxptr = wasm.exports.open()

    const queryAndShow = (q) => {
        console.log( query(wasm, ctxptr, q) )
    }

    try {
        queryAndShow(`
            mutation {
                create(input: { id: "item-1", value: 12 }) {
                    id
                }
            }
        `)

        queryAndShow(`
            mutation {
                create(input: { id: "item-2", value: 34 }) {
                    id
                }
            }
        `)

        queryAndShow(`
            query {
                find(id: "item-1") {
                    id
                    value
                }
            }
        `)

        queryAndShow(`
            {
                find(id: "item-2") {
                    id
                    value
                }
            }
        `)

        queryAndShow(`
            {
                find(id: "item-3") {
                    id
                }
            }
        `)
    } finally {
        wasm.exports.close(ctxptr)
    }
}

run().catch(err => console.error(err))
