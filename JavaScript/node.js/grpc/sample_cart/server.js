const grpc = require('grpc')
const protoLoader = require('@grpc/proto-loader')

const items = {
    'item1': {item_id: 'item1', price: 10},
    'item2': {item_id: 'item2', price: 200},
    'item3': {item_id: 'item3', price: 3000}
}

const carts = {}

const pd = protoLoader.loadSync('./cart.proto', {
    keepCase: true,
    defaults: true
})

const proto = grpc.loadPackageDefinition(pd).sample.cart

console.log(proto)

const findOrCreateCart = cartId => {
    if (!carts[cartId]) {
        carts[cartId] = {items: []}
    }

    return carts[cartId]
}

const addItem = (ctx, callback) => {
    const cartId = ctx.request.cart_id
    const itemId = ctx.request.item_id
    const qty = ctx.request.qty

    if (qty <= 0) {
        callback({code: grpc.status.INVALID_ARGUMENT, message: 'qty <= 0'})
        return
    }

    if (!items[itemId]) {
        callback({code: grpc.status.INVALID_ARGUMENT, message: `${itemId} not found`})
        return
    }

    const cart = findOrCreateCart(cartId)
    const targetItem = cart.items.filter(it => it.item.item_id == itemId)

    if (targetItem.length > 0) {
        targetItem[0].qty += qty
    }
    else {
        cart.items.push({item: items[itemId], qty: qty})
    }

    callback()
}

const removeItem = (ctx, callback) => {
    const cartId = ctx.request.cart_id
    const itemId = ctx.request.item_id

    const cart = findOrCreateCart(cartId)
    cart.items = cart.items.filter(it => it.item.item_id != itemId)

    callback()
}

const getCart = (ctx, callback) => {
    const cartId = ctx.request.cart_id

    callback(null, findOrCreateCart(cartId))
}

const server = new grpc.Server()

server.addService(proto.CartService.service, {
    AddItem: addItem,
    RemoveItem: removeItem,
    GetCart: getCart
})

server.bind('127.0.0.1:50051', grpc.ServerCredentials.createInsecure())
server.start()
