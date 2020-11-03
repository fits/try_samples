
const { Item, AddedItem, RemovedItem, ItemEvent } = require('./generated/proto/item_pb')
const { ItemManageService } = require('./generated/proto/item_grpc_pb')
const { Empty } = require('google-protobuf/google/protobuf/empty_pb')

const EventEmitter = require('events')
const grpc = require('@grpc/grpc-js')

const emitter = new EventEmitter()
const server = new grpc.Server()

let store = []
let clientList = []

const findItem = id => store.find(i => i.getItemId() == id)

const publishEvent = event => clientList.forEach(c => c.write(event))

emitter.on('added-item', params => {
    console.log(`*** added-item: ${JSON.stringify(params)}`)

    const event = new ItemEvent()
    event.setAdded(new AddedItem([params.itemId, params.price]))

    publishEvent(event)
})

emitter.on('removed-item', params => {
    console.log(`*** removed-item: ${JSON.stringify(params)}`)

    const event = new ItemEvent()
    event.setRemoved(new RemovedItem([params.itemId]))

    publishEvent(event)
})


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

            emitter.emit('added-item', { itemId, price })
        }
        else {
            callback({ code: grpc.status.ALREADY_EXISTS, details: 'exists item' })
        }
    },
    removeItem(ctx, callback) {
        const itemId = ctx.request.getItemId()
        const item = findItem(itemId)

        if (item) {
            store = store.filter(i => i.getItemId() != itemId)

            callback(null, new Empty())

            emitter.emit('removed-item', { itemId })
        }
        else {
            callback({ code: grpc.status.NOT_FOUND, details: 'item not found' })
        }
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
    },
    subscribe(ctx) {
        console.log('*** subscribed')

        clientList.push(ctx)

        ctx.on('cancelled', () => {
            console.log('*** unsubscribed')
            clientList = clientList.filter(c => c != ctx)
        })
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
