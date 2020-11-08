
const grpc = require('@grpc/grpc-js')

const { Item, AddedItem, RemovedItem, ItemEvent } = require('./generated/proto/item_pb')
const { ItemManageService } = require('./generated/proto/item_grpc_pb')
const { Empty } = require('google-protobuf/google/protobuf/empty_pb')

let store = []
let subscribeList = []

const findItem = itemId => store.find(i => i.getItemId() == itemId)

const addItem = (itemId, price) => {
    if (findItem(itemId)) {
        return undefined
    }

    const item = new Item([itemId, price])

    store.push(item)

    return item
}

const removeItem = itemId => {
    const item = findItem(itemId)

    if (item) {
        store = store.filter(i => i.getItemId() != item.getItemId())
    }

    return item
}

const createAddedEvent = (itemId, price) => {
    const event = new ItemEvent()
    event.setAdded(new AddedItem([itemId, price]))

    return event
}

const createRemovedEvent = itemId => {
    const event = new ItemEvent()
    event.setRemoved(new RemovedItem([itemId]))

    return event
}

const publishEvent = event => {
    console.log(`*** publish event: ${JSON.stringify(event.toObject())}`)
    subscribeList.forEach(s => s.write(event))
}

const server = new grpc.Server()

server.addService(ItemManageService, {
    addItem(call, callback) {
        const itemId = call.request.getItemId()
        const price = call.request.getPrice()

        const item = addItem(itemId, price)

        if (item) {
            callback(null, new Empty())
            publishEvent(createAddedEvent(itemId, price))
        }
        else {
            const err = { code: grpc.status.ALREADY_EXISTS, details: 'exists item' }
            callback(err)
        }
    },
    removeItem(call, callback) {
        const itemId = call.request.getItemId()

        if (removeItem(itemId)) {
            callback(null, new Empty())
            publishEvent(createRemovedEvent(itemId))
        }
        else {
            const err = { code: grpc.status.NOT_FOUND, details: 'item not found' }
            callback(err)
        }
    },
    getItem(call, callback) {
        const itemId = call.request.getItemId()
        const item = findItem(itemId)

        if (item) {
            callback(null, item)
        }
        else {
            const err = { code: grpc.status.NOT_FOUND, details: 'item not found' }
            callback(err)
        }
    },
    subscribe(call) {
        console.log('*** subscribed')
        subscribeList.push(call)

        call.on('cancelled', () => {
            console.log('*** unsubscribed')
            subscribeList = subscribeList.filter(s => s != call)
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
