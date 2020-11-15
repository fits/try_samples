
const grpc = require('@grpc/grpc-js')

const { AddItemRequest, ItemRequest } = require('./generated/proto/item_pb')
const { ItemManageClient } = require('./generated/proto/item_grpc_pb')

const id = process.argv[2]

const client = new ItemManageClient(
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

const addItem = promisify(client, 'addItem')
const removeItem = promisify(client, 'removeItem')
const getItem = promisify(client, 'getItem')

const printItem = item => {
    console.log(`id = ${item.getItemId()}, price = ${item.getPrice()}`)
}

const run = async () => {
    await addItem(new AddItemRequest([`${id}_item-1`, 100]))

    const item1 = await getItem(new ItemRequest([`${id}_item-1`]))
    printItem(item1)

    await addItem(new AddItemRequest([`${id}_item-2`, 20]))

    const item2 = await getItem(new ItemRequest([`${id}_item-2`]))
    printItem(item2)

    await addItem(new AddItemRequest([`${id}_item-1`, 50]))
        .catch(err => console.error(`*** ERROR = ${err.message}`))

    await removeItem(new ItemRequest([`${id}_item-1`]))

    await getItem(new ItemRequest([`${id}_item-1`]))
        .catch(err => console.error(`*** ERROR = ${err.message}`))

    await removeItem(new ItemRequest([`${id}_item-2`]))
}

run().catch(err => console.error(err))
