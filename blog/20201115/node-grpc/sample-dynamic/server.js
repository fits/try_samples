
const protoLoader = require('@grpc/proto-loader')
const grpc = require('@grpc/grpc-js')

const protoFile = './proto/item.proto'

const pd = protoLoader.loadSync(protoFile)
const proto = grpc.loadPackageDefinition(pd)

let store = []
let subscribeList = []

const findItem = itemId => store.find(i => i.itemId == itemId)

const addItem = (itemId, price) => {
    if (findItem(itemId)) {
        return undefined
    }

    const item = { itemId, price }

    store.push(item)

    return item
}

const removeItem = itemId => {
    const item = findItem(itemId)

    if (item) {
        store = store.filter(i => i.itemId != item.itemId)
    }

    return item
}

const publishEvent = event => {
    console.log(`*** publish event: ${JSON.stringify(event)}`)
    subscribeList.forEach(s => s.write(event))
}

const server = new grpc.Server()

server.addService(proto.item.ItemManage.service, {
    AddItem(call, callback) {
        const itemId = call.request.itemId
        const price = call.request.price

        const item = addItem(itemId, price)

        if (item) {
            callback()
            publishEvent({ added: { itemId, price }})
        }
        else {
            const err = { code: grpc.status.ALREADY_EXISTS, details: 'exists item' }
            callback(err)
        }
    },
    RemoveItem(call, callback) {
        const itemId = call.request.itemId

        if (removeItem(itemId)) {
            callback()
            publishEvent({ removed: { itemId }})
        }
        else {
            const err = { code: grpc.status.NOT_FOUND, details: 'item not found' }
            callback(err)
        }
    },
    GetItem(call, callback) {
        const itemId = call.request.itemId
        const item = findItem(itemId)

        if (item) {
            callback(null, item)
        }
        else {
            const err = { code: grpc.status.NOT_FOUND, details: 'item not found' }
            callback(err)
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
