const grpc = require('grpc')
const protoLoader = require('@grpc/proto-loader')

const pd = protoLoader.loadSync('./cart.proto', {
    keepCase: true,
    defaults: true
})

const proto = grpc.loadPackageDefinition(pd).sample.cart

const client = new proto.CartService(
    '127.0.0.1:50051', 
    grpc.credentials.createInsecure()
)

console.log(client.GetCart)

const promisify = (obj, methodName) => args => new Promise((resolve, reject) => {
    obj[methodName](args, (err, res) => {
        if (err) {
            reject(err)
        }
        else {
            resolve(res)
        }
    })
})

const getCart = promisify(client, 'GetCart')
const addItem = promisify(client, 'AddItem')
const removeItem = promisify(client, 'RemoveItem')

const logCart = async (cartId) => {
    const cart = await getCart({cart_id: cartId})
    console.log(JSON.stringify(cart))
}

const action = async () => {
    const cartId = 'a1'

    await logCart(cartId)

    await addItem({cart_id: cartId, item_id: 'item2', qty: 1})
    await logCart(cartId)

    await addItem({cart_id: cartId, item_id: 'item3', qty: 1})
    await logCart(cartId)

    await addItem({cart_id: cartId, item_id: 'item2', qty: 2})
    await logCart(cartId)

    await removeItem({cart_id: cartId, item_id: 'item3'})
    await logCart(cartId)
}

const errAction = async () => {
    const cartId = 'b2'

    await logCart(cartId)

    await addItem({cart_id: cartId, item_id: 'item1', qty: -1})
    await logCart(cartId)
}

const actions = async () => {
    await action()

    console.log('-----')

    await errAction()
}

actions().catch(err => console.error(err))
