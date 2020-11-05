
const protoLoader = require('@grpc/proto-loader')
const grpc = require('@grpc/grpc-js')
const EventEmitter = require('events')

const protoFile = './proto/item.proto'
const opts = {}

const pd = protoLoader.loadSync(protoFile, opts)
const proto = grpc.loadPackageDefinition(pd)

let store = []
let subscribeList = []

const emitter = new EventEmitter()
const server = new grpc.Server()

const findItem = id => store.find(i => i.itemId == id)

const publishEvent = event => subscribeList.forEach(s => s.write(event))

emitter.on('added-item', event => {
    console.log(`*** added-item: ${JSON.stringify(event)}`)

    publishEvent({ added: event })
})

emitter.on('removed-item', event => {
    console.log(`*** removed-item: ${JSON.stringify(event)}`)

    publishEvent({ removed: event })
})

server.addService(proto.item.ItemManage.service, {
    AddItem(call, callback) {
        const itemId = call.request.itemId
        const price = call.request.price

        if (!findItem(itemId)) {
            const item = { itemId, price }

            store.push(item)

            callback()

            emitter.emit('added-item', { itemId, price })
        }
        else {
            callback({ code: grpc.status.ALREADY_EXISTS, details: 'exists item' })
        }
    },
    RemoveItem(call, callback) {
        const itemId = call.request.itemId
        const item = findItem(itemId)

        if (item) {
            store = store.filter(i => i.itemId != itemId)

            callback()

            emitter.emit('removed-item', { itemId })
        }
        else {
            callback({ code: grpc.status.NOT_FOUND, details: 'item not found' })
        }
    },
    GetItem(call, callback) {
        const itemId = call.request.itemId
        const item = findItem(itemId)

        if (item) {
            callback(null, item)
        }
        else {
            callback({ code: grpc.status.NOT_FOUND, details: 'item not found' })
        }
    },
    Subscribe(call) {
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
