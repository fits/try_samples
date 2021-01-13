
const fs = require('fs')

const wasmFile = process.argv[2]

globalThis.crypto = {
    getRandomValues(b) {}
}

new Function(fs.readFileSync(`${process.env.GOROOT}/misc/wasm/wasm_exec.js`))()

const run = async () => {
    const go = new globalThis.Go()

    const handler = {
        get: function(target, prop, receiver) {
            const f = Reflect.get(...arguments)

            return sp => {
                console.log(`* call ${prop}: ${sp}`)
                return f(sp)
            }
        }
    }

    go.importObject.go = new Proxy(go.importObject.go, handler)

    const wasm = await WebAssembly.instantiate(
        fs.readFileSync(wasmFile), 
        go.importObject
    )

    go.run(wasm.instance)
}

run().catch(err => console.error(err))
