
const protoLoader = require('@grpc/proto-loader')
const grpc = require('@grpc/grpc-js')

const protoFile = './proto/item.proto'
const opts = {}

const pd = protoLoader.loadSync(protoFile, opts)
const proto = grpc.loadPackageDefinition(pd)

const id = process.argv[2]

const client = new proto.item.ItemManage(
    '127.0.0.1:50051',
    grpc.credentials.createInsecure()
)

const promisify = (obj, methodName) => args => 
    new Promise((resolve, reject) => {
        obj[methodName](args, (err, res) => {
            if (err) {
                reject(err)
            }
            else {
                resolve(res)
            }
        })
    })

const addItem = promisify(client, 'AddItem')
const removeItem = promisify(client, 'RemoveItem')
const getItem = promisify(client, 'GetItem')

const printItem = item => {
    console.log(`id = ${item.itemId}, price = ${item.price}`)
}

const run = async () => {
    await addItem({ itemId: `${id}_item-1`, price: 100 })
        .catch(err => console.error(err.message))

    const item1 = await getItem({ itemId: `${id}_item-1` })
    printItem(item1)

    await addItem({ itemId: `${id}_item-2`, price: 20 })
        .catch(err => console.error(err.message))

    const item2 = await getItem({ itemId: `${id}_item-2` })
    printItem(item2)

    await getItem({ itemId: `${id}_item-2` })
        .catch(err => console.error(err.message))

    await addItem({ itemId: `${id}_item-1`, price: 50 })
        .catch(err => console.error(err.message))

    await removeItem({ itemId: `${id}_item-1` })

    await getItem({ itemId: `${id}_item-1` })
        .catch(err => console.error(err.message))

    await removeItem({ itemId: `${id}_item-2` })
}

run().catch(err => console.error(err))
