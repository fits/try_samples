
const protobuf = require('protobufjs')

const protoFile = 'item.proto'

const run = async () => {
    const root = await protobuf.load(protoFile)
    const Item = root.lookupType('sample.item.Item')

    const d = Item.create({ itemId: 'item-1', price: 1000 })
    console.log(d)

    const buf = Item.encode(d).finish()
    console.log(buf)

    const d2 = Item.decode(buf)
    console.log(d2)
}

run().catch(err => console.error(err))
