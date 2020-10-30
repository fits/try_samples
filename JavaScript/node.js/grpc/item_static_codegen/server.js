
const { Item } = require('./generated/proto/item_pb')
const { ItemManageService } = require('./generated/proto/item_grpc_pb')
const { Empty } = require('google-protobuf/google/protobuf/empty_pb')

const grpc = require('@grpc/grpc-js')

const server = new grpc.Server()

let store = []

const findItem = id => store.find(i => i.getItemId() == id)

server.addService(ItemManageService, {
    addItem(ctx, callback) {
        const itemId = ctx.request.getItemId()
        const price = ctx.request.getPrice()

        if (!findItem(itemId)) {
            const item = new Item()
            item.setItemId(itemId)
            item.setPrice(price)
            // const item = new Item([itemId, price])
            store.push(item)

            callback(null, new Empty())
        }
        else {
            callback({ code: grpc.status.ALREADY_EXISTS, details: 'exists item' })
        }
    },
    removeItem(ctx, callback) {
        const itemId = ctx.request.getItemId()

        store = store.filter(i => i.getItemId() != itemId)

        callback(null, new Empty())
    },
    getItem(ctx, callback) {
        const itemId = ctx.request.getItemId()
        const item = findItem(itemId)

        if (item) {
            callback(null, item)
        }
        else {
            callback({ code: grpc.status.NOT_FOUND, details: 'item not found' })
        }
    }
})

server.bindAsync(
    '127.0.0.1:50051',
    grpc.ServerCredentials.createInsecure(),
    (err, port) => {
        if (err) {
            console.error(err)
            return
        }

        console.log(`start server: ${port}`)

        server.start()
    }
)
