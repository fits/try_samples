
const protobuf = require('protobufjs')
const fs = require('fs')

const wasmFile = process.argv[2]
const protoFile = '../proto/item.proto'

const restoreObject = (wasmInstance, ptr, len, decoder) => {
    if (!wasmInstance) return null

    const memory = wasmInstance.exports.memory.buffer
    const buf = new Uint8Array(memory, ptr, len)
    
    return decoder(buf)
}

const processMessage = (wasmInstance, type, params, func) => {
    if (!wasmInstance) return null

    const msg = type.create(params)
    console.log(msg)

    const buf = type.encode(msg).finish()

    const ptr = wasmInstance.exports._malloc(buf.length)

    const memory = wasmInstance.exports.memory.buffer
    new Uint8Array(memory).set(buf, ptr)

    func(ptr, buf.length)
}

const run = async () => {
    const root = await protobuf.load(protoFile)
    const Item = root.lookupType('sample.item.Item')

    let instance = null

    const importObject = {
        sample: {
            log: (ptr, len) => {
                const msg = restoreObject(
                    instance, ptr, len, 
                    buf => new TextDecoder('utf-8').decode(buf)
                )

                console.log(`log: ${msg}`)
            },
            print_item: (ptr, len) => {
                const item = restoreObject(
                    instance, ptr, len, 
                    buf => Item.decode(buf)
                )

                console.log(item)
            }
        }
    }

    const buf = fs.readFileSync(wasmFile)

    const wasm = await WebAssembly.instantiate(buf, importObject)
    instance = wasm.instance

    console.log('--- send_item ---')

    const params = { itemId: 'item-A', price: 120 }
    processMessage(instance, Item, params, instance.exports.send_item)

    console.log('--- main ---')

    instance.exports.main()
}

run().catch(err => console.error(err))
