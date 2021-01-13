const fs = require('fs')

const wasmFile = process.argv[2]

const run = async () => {
    let inst
    let mem

    const getInt64 = (addr) => {
        const low = mem.getUint32(addr + 0, true);
        const high = mem.getInt32(addr + 4, true);
        return low + high * 4294967296;
    }

    const importObject = {
        go: {
            debug: (sp) => {},
            "runtime.resetMemoryDataView": (sp) => {},
            "runtime.wasmExit": (sp) => {
                const code = mem.getInt32(sp + 8, true)
                console.log(`* wasmExit: code = ${code}`)
            },
            "runtime.wasmWrite": (sp) => {
                const fd = getInt64(sp + 8);
                console.log(`* wasmWrite fd = ${fd}`)

                const p = getInt64(sp + 16)
                const n = mem.getInt32(sp + 24, true)

                const a = new Uint8Array(inst.exports.mem.buffer, p, n)
                const s = new TextDecoder('utf-8').decode(a)

                process.stdout.write(s)
            },
            "runtime.nanotime1": (sp) => {},
            "runtime.walltime1": (sp) => {},
            "runtime.scheduleTimeoutEvent": (sp) => {},
            "runtime.clearTimeoutEvent": (sp) => {},
            "runtime.getRandomData": (sp) => {},
        }
    }

    const wasm = await WebAssembly.instantiate(fs.readFileSync(wasmFile), importObject)

    inst = wasm.instance
    mem = new DataView(inst.exports.mem.buffer)

    inst.exports.run()
}

run().catch(err => console.error(err))
