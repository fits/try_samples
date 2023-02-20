import { readAll } from "https://deno.land/std@0.177.0/streams/read_all.ts"

const file = await Deno.open(Deno.args[0])

const buf = await readAll(file)

file.close()

let wasmInstance: WebAssembly.Instance | undefined

const wasiObj = {
    importObject: {
        '$root': {
            log: (fd, ptr, len) => {
                console.log(`*** call log: fd=${fd}, ptr=${ptr}, len=${len}`)

                const memory = wasmInstance?.exports.memory['buffer']

                if (memory) {
                    const buf = new Uint8Array(memory, ptr, len)
                    const msg = new TextDecoder('utf-8').decode(buf)

                    console.log(msg)    
                }
            }
        },
        wasi_snapshot_preview1: {
            fd_write: () => {},
            proc_exit: () => {},
            fd_prestat_get: () => {},
            fd_prestat_dir_name: () => {},
            environ_sizes_get: () => {},
            environ_get: () => {}
        }
    }
}

const res = await WebAssembly.instantiate(buf, wasiObj.importObject)
wasmInstance = res.instance

if (wasmInstance.exports.run instanceof Function) {
    wasmInstance.exports.run()
}
