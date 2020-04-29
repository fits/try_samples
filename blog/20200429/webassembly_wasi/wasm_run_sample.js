
const fs = require('fs')

const WASI_ESUCCESS = 0;
const WASI_EBADF = 8; // Bad file descriptor

const wasmFile = process.argv[2]

const wasiObj = {
    wasmInstance: null,
    importObject: {
        wasi_snapshot_preview1: {
            fd_write: (fd, iovs, iovsLen, nwritten) => {
                console.log(`*** call fd_write: fd=${fd}, iovs=${iovs}, iovsLen=${iovsLen}, nwritten=${nwritten}`)

                const memory = wasiObj.wasmInstance.exports.memory.buffer
                const view = new DataView(memory)
                
                const sizeList = Array.from(Array(iovsLen), (v, i) => {
                    const ptr = iovs + i * 8
                    
                    const bufStart = view.getUint32(ptr, true)
                    const bufLen = view.getUint32(ptr + 4, true)
                    
                    const buf = new Uint8Array(memory, bufStart, bufLen)
                    const msg = String.fromCharCode(...buf)
                    
                    process.stdout.write(msg)
                    
                    return buf.byteLength
                })
                
                const totalSize = sizeList.reduce((acc, v) => acc + v)
                view.setUint32(nwritten, totalSize, true)
                
                return WASI_ESUCCESS
            },
            proc_exit: () => {},
            fd_prestat_get: (fd, bufPtr) => { 
                console.log(`*** call fd_prestat_get: fd=${fd}, bufPtr=${bufPtr}`)
                return WASI_EBADF
            },
            fd_prestat_dir_name: () => {},
            environ_sizes_get: () => {},
            environ_get: () => {}
        }
    }
}

const buf = fs.readFileSync(wasmFile)

WebAssembly.instantiate(buf, wasiObj.importObject)
    .then(res => {
        wasiObj.wasmInstance = res.instance
        wasiObj.wasmInstance.exports._start()
    })
    .catch(err => console.error(err))
